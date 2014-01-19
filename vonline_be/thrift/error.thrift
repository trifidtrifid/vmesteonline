namespace * com.vmesteonline.be

enum Error{
  IncorrectParametrs = 1,
  RegistrationAlreadyExist,
  
}
exception InvalidOperation {
  1: Error what,
  2: string why
}


