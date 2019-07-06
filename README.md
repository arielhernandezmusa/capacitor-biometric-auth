# Capacitor Biometric Auth

[![Build Status](https://travis-ci.org/arielhernandezmusa/capacitor-biometric.svg?branch=master)](https://travis-ci.org/arielhernandezmusa/capacitor-biometric)

## Installation

* `npm i capacitor-biometric-auth`
* `yarn add capacitor-biometric-auth`

## Usage

```ts
import { Plugins } from "@capacitor/core";

const { BiometricAuth } = Plugins;

const available = await BiometricAuth.isAvailable()

if (available.has) {
  const authResult = await BiometricAuth.verify()
  if (authResult.verified) {
    // success authentication
  } else {
    // fail authentication
  }
} else {
  // biometric not available
}
 ```
