declare module "@capacitor/core" {
  interface PluginRegistry {
    BiometricAuth: BiometricAuthPlugin;
  }
}

export interface ErrorCode {
  error: number;
  description: string;
}

export interface AvailableOptions {
  has: boolean;
  status: ErrorCode;
}

export interface VerifyOptions {
  verified: boolean;
  status: ErrorCode;
}

export interface BiometricAuthPlugin {
  isAvailable(): Promise<AvailableOptions>;
  verify(options: { reason: string }): Promise<VerifyOptions>;
}