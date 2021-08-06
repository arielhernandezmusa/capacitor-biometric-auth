
  Pod::Spec.new do |s|
    s.name = 'CapacitorBiometricAuth'
    s.version = '0.0.1'
    s.summary = 'Complete'
    s.license = 'MIT'
    s.homepage = 'https://github.com/arielhernandezmusa/capacitor-biometric.git'
    s.author = 'Ariel Hernandez Musa'
    s.source = { :git => 'https://github.com/arielhernandezmusa/capacitor-biometric.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '12.0'
    s.dependency 'Capacitor'
  end
