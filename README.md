# Capacitor Biometric Auth
<a href='https://www.npmjs.org/package/capacitor-biometric-auth' target='_blank'><img height='21' style='border:0px;height:21px;' src='https://img.shields.io/npm/dt/capacitor-biometric-auth.svg?label=NPM+Downloads' border='0' alt='NPM Downloads' /></a>
![CI](https://github.com/arielhernandezmusa/capacitor-biometric-auth/workflows/CI/badge.svg)

## Installation

* `npm i capacitor-biometric-auth`
* `yarn add capacitor-biometric-auth`

## Setup
Don't for get to run ```npx cap sync``` before doing the next steps.

### Android
To get android working please add this code to your MainActivity file.

> MainActivity.java
```diff
++ import com.ahm.capacitor.biometric.BiometricAuth;

this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
++ add(BiometricAuth.class);
}});
```

### IOS

@TODO

## Usage

```ts
import { Plugins } from "@capacitor/core";

const { BiometricAuth } = Plugins;

const available = await BiometricAuth.isAvailable()

if (available.has) {
  const authResult = await BiometricAuth.verify({...})
  if (authResult.verified) {
    // success authentication
  } else {
    // fail authentication
  }
} else {
  // biometric not available
}
 ```


 ## Methods

 #### verify(options)
 Open biometric popup

 | option | values | decription |
 | --- | --- | --- |
 | reason | any string | Popup label for iOS|
 | title | any string | Title of prompt in Android |
 | subTitle | any string | Subtitle of prompt in Android |
 | description | any string | Description of prompt in Android |
 | cancel | any string | Text for cancel button on prompt in Android |
 | deviceCredentialAllowed | boolean | Allows fallback to PIN/Password/Pattern

 ```ts
const result = await BiometricAuth.verify({reason: "Message ..."})
```

**result**
```javascript
{
  verified: true // true if biometric auth was succes or false otherwise,
  status: {} // an object with errors matching biometric auth fails (on if verified === false)
}
```

**status**

|error|description|
|-|-|
| 10 | The user failed to provide valid credentials |
| 11 | Authentication was cancelled by application |
| 12 | The context is invalid |
| 13 | Not interactive |
| 14 | Passcode is not set on the device |
| 15 | Authentication was cancelled by the system |
| 16 | The user did cancel |
| 17 | The user chose to use the fallback |

### isAvailable()

Checks if biometric is enabled

```ts
const result = await BiometricAuth.isAvailable()
```

**result**
```javascript
{
  has: true, // true if has biometric auth enabled, false otherwise
  status: {...} // an object with errors
}
```

**status**

|error|description|
|-|-|
| 1 | Biometric not available |
| 2 | Authentication could not continue because the user has been locked out of biometric authentication, due to failing authentication too many times.|
| 3 | Authentication could not start because the user has not enrolled in biometric authentication.|
