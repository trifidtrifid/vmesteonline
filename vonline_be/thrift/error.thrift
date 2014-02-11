namespace * com.vmesteonline.be

enum VoError{
  GeneralError,
  IncorrectParametrs,
  RegistrationAlreadyExist,
  NotAuthorized,
  IncorectLocationCode,
//Shop specific messages   
  ShopNotOrderDate,
}
exception InvalidOperation {
  1: VoError what,
  2: string why
}


