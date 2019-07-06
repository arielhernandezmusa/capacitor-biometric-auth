import { WebPlugin } from '@capacitor/core';
import { BiometricAuthPlugin } from './definitions';

export class BiometricAuthWeb extends WebPlugin implements BiometricAuthPlugin {
  constructor() {
    super({
      name: 'BiometricAuth',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const BiometricAuth = new BiometricAuthWeb();

export { BiometricAuth };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BiometricAuth);
