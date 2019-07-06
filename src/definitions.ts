declare module "@capacitor/core" {
  interface PluginRegistry {
    BiometricAuth: BiometricAuthPlugin;
  }
}

export interface BiometricAuthPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
