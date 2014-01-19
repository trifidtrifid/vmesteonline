namespace * com.vmesteonline.be

enum Error{
  GeneralError,
  IncorrectParametrs,
  RegistrationAlreadyExist,
  NotAuthorized,
  
}
exception InvalidOperation {
  1: Error what,
  2: string why
}


