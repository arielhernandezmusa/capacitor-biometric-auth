import { WebPlugin } from '@capacitor/core';
import { AvailableOptions, VerifyOptions, BiometricAuthPlugin } from './definitions';

export class BiometricAuthWeb extends WebPlugin implements BiometricAuthPlugin {
  constructor() {
    super({
      name: 'BiometricAuth',
      platforms: ['web']
    });
  }

  async isAvailable(): Promise<AvailableOptions> {
    return new Promise(() => {});
  }

  async verify(options: { reason: string }): Promise<VerifyOptions> {
    console.log('OPTIONS', options)
    return new Promise(() => {});
  }
}

const BiometricAuth = new BiometricAuthWeb();

export { BiometricAuth };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BiometricAuth);
