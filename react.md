Here’s a clean starter. It uses Expo Router auth gating, root session bootstrap, and secure session persistence patterns aligned with Expo Router’s auth guide and React Navigation auth-flow guidance. [docs.expo](https://docs.expo.dev/router/advanced/authentication/)

## Tree

```text
.
├─ app/
│  ├─ _layout.tsx
│  ├─ index.tsx
│  ├─ sign-in.tsx
│  └─ (app)/
│     ├─ _layout.tsx
│     ├─ home.tsx
│     └─ settings.tsx
├─ src/
│  ├─ auth/
│  │  ├─ AuthProvider.tsx
│  │  ├─ auth.types.ts
│  │  └─ useAuth.ts
│  ├─ lib/
│  │  ├─ api.ts
│  │  └─ storage.ts
│  ├─ ui/
│  │  ├─ Screen.tsx
│  │  └─ Button.tsx
│  └─ theme/
│     └─ tokens.ts
├─ .env
└─ app.json
```

`app/` stays routing-only. `src/` holds session logic, storage, and reusable UI. [dev](https://dev.to/aaronksaunders/expo-router-v2-authentication-flow-with-appwrite-jee)

## Auth model

Use one session source:

- `loading`
- `signedOut`
- `signedIn`
- `error`

Session loads from secure storage on boot, then app decides whether to show auth or private routes. Expo Router recommends keeping splash visible until auth load completes. React Navigation’s auth-flow doc uses the same basic rule: load auth state first, then render auth or main app. [reactnavigation](https://reactnavigation.org/docs/auth-flow/)

## `src/auth/auth.types.ts`

```ts
export type AuthStatus = 'loading' | 'signedOut' | 'signedIn' | 'error';

export type Session = {
  accessToken: string;
  refreshToken?: string;
  userId: string;
};

export type AuthState = {
  status: AuthStatus;
  session: Session | null;
  error?: string | null;
};
```

## `src/lib/storage.ts`

```ts
import * as SecureStore from 'expo-secure-store';

const KEY = 'session';

export async function saveSession(value: string) {
  await SecureStore.setItemAsync(KEY, value);
}

export async function loadSession() {
  return SecureStore.getItemAsync(KEY);
}

export async function clearSession() {
  await SecureStore.deleteItemAsync(KEY);
}
```

Use secure storage for tokens/session material, not plain AsyncStorage, consistent with Expo auth/security guidance. [youtube](https://www.youtube.com/watch?v=LbitQCnppKw)

## `src/auth/AuthProvider.tsx`

```tsx
import React from 'react';
import { clearSession, loadSession, saveSession } from '@/src/lib/storage';
import type { AuthState, Session } from './auth.types';

type AuthContextValue = AuthState & {
  signIn: (session: Session) => Promise<void>;
  signOut: () => Promise<void>;
  hydrate: () => Promise<void>;
};

const AuthContext = React.createContext<AuthContextValue | null>(null);

const initialState: AuthState = {
  status: 'loading',
  session: null,
  error: null,
};

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = React.useState<AuthState>(initialState);

  const hydrate = React.useCallback(async () => {
    try {
      setState((s) => ({ ...s, status: 'loading', error: null }));
      const raw = await loadSession();
      if (!raw) {
        setState({ status: 'signedOut', session: null, error: null });
        return;
      }
      const session = JSON.parse(raw) as Session;
      setState({ status: 'signedIn', session, error: null });
    } catch (e) {
      setState({
        status: 'error',
        session: null,
        error: e instanceof Error ? e.message : 'auth hydrate failed',
      });
    }
  }, []);

  const signIn = React.useCallback(async (session: Session) => {
    await saveSession(JSON.stringify(session));
    setState({ status: 'signedIn', session, error: null });
  }, []);

  const signOut = React.useCallback(async () => {
    await clearSession();
    setState({ status: 'signedOut', session: null, error: null });
  }, []);

  React.useEffect(() => {
    hydrate();
  }, [hydrate]);

  return (
    <AuthContext.Provider value={{ ...state, signIn, signOut, hydrate }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext() {
  const ctx = React.useContext(AuthContext);
  if (!ctx) throw new Error('useAuthContext must be used inside AuthProvider');
  return ctx;
}
```

This is the simplest sane bootstrap. Later, swap `hydrate()` for a real backend session refresh call. [docs.expo](https://docs.expo.dev/router/advanced/authentication/)

## `app/_layout.tsx`

```tsx
import { Stack, useRouter, useSegments } from 'expo-router';
import { useEffect } from 'react';
import { AuthProvider, useAuthContext } from '@/src/auth/AuthProvider';

function Gate() {
  const { status } = useAuthContext();
  const segments = useSegments();
  const router = useRouter();

  const inAuthGroup = segments[0] === 'sign-in';

  useEffect(() => {
    if (status === 'loading') return;

    const authed = status === 'signedIn';

    if (!authed && !inAuthGroup) router.replace('/sign-in');
    if (authed && inAuthGroup) router.replace('/(app)/home');
  }, [status, inAuthGroup, router]);

  return <Stack screenOptions={{ headerShown: false }} />;
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <Gate />
    </AuthProvider>
  );
}
```

Expo Router supports this “root gate + protected group” pattern directly. Keep it centralized so private routes stay private. [docs.expo](https://docs.expo.dev/router/advanced/authentication/)

## `app/index.tsx`

```tsx
import { Redirect } from 'expo-router';

export default function Index() {
  return <Redirect href="/sign-in" />;
}
```

This avoids a blank root and gives one clear entry path.

## `app/sign-in.tsx`

```tsx
import { View, Text, Pressable } from 'react-native';
import { useAuthContext } from '@/src/auth/AuthProvider';

export default function SignIn() {
  const { signIn, status } = useAuthContext();

  const onLogin = async () => {
    await signIn({
      accessToken: 'demo-token',
      userId: 'u_123',
    });
  };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Sign In</Text>
      <Pressable onPress={onLogin} disabled={status === 'loading'}>
        <Text>Login</Text>
      </Pressable>
    </View>
  );
}
```

Replace the demo token with your backend login call, then store the real session after success. [supabase](https://supabase.com/blog/react-native-authentication)

## `app/(app)/_layout.tsx`

```tsx
import { Stack } from 'expo-router';

export default function AppLayout() {
  return <Stack screenOptions={{ headerShown: false }} />;
}
```

This group is the private area. Put all signed-in screens under `(app)` so auth gating stays obvious. [docs.expo](https://docs.expo.dev/router/advanced/authentication/)

## `app/(app)/home.tsx`

```tsx
import { View, Text } from 'react-native';

export default function Home() {
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Home</Text>
    </View>
  );
}
```

## Next wiring

Add these next, in order:

- backend auth client.
- token refresh on app foreground.
- logout button in private shell.
- deep link handling for magic links / email verify.
- responsive nav shell for tablet/web.

For Supabase, the clean path is `createClient(..., { auth: { persistSession: true, autoRefreshToken: true, detectSessionInUrl: false } })`, with secure storage backing if you need stronger persistence control. For Firebase, use `onAuthStateChanged` inside the provider and map that to the same `AuthState` shape. [reddit](https://www.reddit.com/r/reactnative/comments/1roa2ql/how_do_you_handle_auth_flow_in_expo_router/)

Want the next layer: a **responsive `(app)` shell with sidebar/tab switch + API client + protected logout flow**?
