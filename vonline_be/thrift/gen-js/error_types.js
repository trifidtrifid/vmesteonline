//
// Autogenerated by Thrift Compiler (1.0.0-dev)
//
// DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//


if (typeof com === 'undefined') {
  com = {};
}
if (typeof com.vmesteonline === 'undefined') {
  com.vmesteonline = {};
}
if (typeof com.vmesteonline.be === 'undefined') {
  com.vmesteonline.be = {};
}
com.vmesteonline.be.VoError = {
'GeneralError' : 0,
'IncorrectParametrs' : 1,
'RegistrationAlreadyExist' : 2,
'NotAuthorized' : 3,
'IncorectLocationCod' : 4
};
com.vmesteonline.be.InvalidOperation = function(args) {
  this.what = null;
  this.why = null;
  if (args) {
    if (args.what !== undefined) {
      this.what = args.what;
    }
    if (args.why !== undefined) {
      this.why = args.why;
    }
  }
};
Thrift.inherits(com.vmesteonline.be.InvalidOperation, Thrift.TException);
com.vmesteonline.be.InvalidOperation.prototype.name = 'InvalidOperation';
com.vmesteonline.be.InvalidOperation.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.I32) {
        this.what = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRING) {
        this.why = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.InvalidOperation.prototype.write = function(output) {
  output.writeStructBegin('InvalidOperation');
  if (this.what !== null && this.what !== undefined) {
    output.writeFieldBegin('what', Thrift.Type.I32, 1);
    output.writeI32(this.what);
    output.writeFieldEnd();
  }
  if (this.why !== null && this.why !== undefined) {
    output.writeFieldBegin('why', Thrift.Type.STRING, 2);
    output.writeString(this.why);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

