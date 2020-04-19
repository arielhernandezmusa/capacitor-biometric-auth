import Foundation
import Capacitor
import LocalAuthentication

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(BiometricAuth)
public class BiometricAuth: CAPPlugin {
    
    @objc func isAvailable(_ call: CAPPluginCall) {
        var authError: NSError?
        let localAuthenticationContext = LAContext()
        localAuthenticationContext.localizedFallbackTitle = "Use Passcode"
        if localAuthenticationContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &authError) {
            call.resolve(["has": true])
        } else {
            guard let error = authError else {
                return
            }
            var errorCode = 0
            if #available(iOS 11.0, macOS 10.13, *) {
                switch error.code {
                case LAError.biometryNotAvailable.rawValue:
                    errorCode = 1
                    
                case LAError.biometryLockout.rawValue:
                    errorCode = 2 //"Authentication could not continue because the user has been locked out of biometric authentication, due to failing authentication too many times."
                    
                case LAError.biometryNotEnrolled.rawValue:
                    errorCode = 3//message = "Authentication could not start because the user has not enrolled in biometric authentication."
                    
                default:
                    errorCode = 999 //"Did not find error code on LAError object"
                }
            }
            else {
                switch error.code {
                case LAError.touchIDLockout.rawValue:
                    errorCode = 2

                case LAError.touchIDNotAvailable.rawValue:
                    errorCode = 1

                case LAError.touchIDNotEnrolled.rawValue:
                    errorCode = 3

                default:
                    errorCode = 999
                }
            }
        
            call.resolve(["has": false, "status": errorCode])
        }
    }
    
    @objc func verify(_ call: CAPPluginCall) {
        let localAuthenticationContext = LAContext()
        let reasonString = call.getString("reason") ?? "To access the secure data"
        localAuthenticationContext.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: reasonString) { success, evaluateError in
            if success {
                call.resolve(["verified": true])
            } else {
                var errorCode = 0
                guard let error = evaluateError else {
                    return
                }
                switch error._code {
                    
                case LAError.authenticationFailed.rawValue:
                    errorCode = 10 //"The user failed to provide valid credentials"
                    
                case LAError.appCancel.rawValue:
                    errorCode = 11 // "Authentication was cancelled by application"
                    
                case LAError.invalidContext.rawValue:
                    errorCode = 12 // "The context is invalid"
                    
                case LAError.notInteractive.rawValue:
                    errorCode = 13 // "Not interactive"
                    
                case LAError.passcodeNotSet.rawValue:
                    errorCode = 14 // "Passcode is not set on the device"
                    
                case LAError.systemCancel.rawValue:
                    errorCode = 15 // "Authentication was cancelled by the system"
                    
                case LAError.userCancel.rawValue:
                    errorCode = 16 // "The user did cancel"
                    
                case LAError.userFallback.rawValue:
                    errorCode = 17 // "The user chose to use the fallback"
                    
                default:
                    errorCode = self.evaluatePolicyFailErrorMessageForLA(errorCode: error._code)
                }
                
                call.reject("Auth failed", nil, ["verified": false, "status": errorCode] as? Error)
            }
        }
    }
    
    @objc func evaluatePolicyFailErrorMessageForLA(errorCode: Int) -> Int {
        var errorCode = 0
        if #available(iOS 11.0, macOS 10.13, *) {
            switch errorCode {
            case LAError.biometryNotAvailable.rawValue:
                errorCode = 1
                
            case LAError.biometryLockout.rawValue:
                errorCode = 2 //"Authentication could not continue because the user has been locked out of biometric authentication, due to failing authentication too many times."
                
            case LAError.biometryNotEnrolled.rawValue:
                errorCode = 3//message = "Authentication could not start because the user has not enrolled in biometric authentication."
                
            default:
                errorCode = 999 //"Did not find error code on LAError object"
            }
        } else {
            switch errorCode {
            case LAError.touchIDLockout.rawValue:
                errorCode = 2
                
            case LAError.touchIDNotAvailable.rawValue:
                errorCode = 1
                
            case LAError.touchIDNotEnrolled.rawValue:
                errorCode = 3
                
            default:
                errorCode = 999
            }
        }
        
        return errorCode;
    }
}
