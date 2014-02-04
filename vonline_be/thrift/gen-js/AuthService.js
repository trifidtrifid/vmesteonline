//
// Autogenerated by Thrift Compiler (1.0.0-dev)
//
// DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//


//HELPER FUNCTIONS AND STRUCTURES

com.vmesteonline.be.AuthService_login_args = function(args) {
  this.email = null;
  this.password = null;
  if (args) {
    if (args.email !== undefined) {
      this.email = args.email;
    }
    if (args.password !== undefined) {
      this.password = args.password;
    }
  }
};
com.vmesteonline.be.AuthService_login_args.prototype = {};
com.vmesteonline.be.AuthService_login_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRING) {
        this.email = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRING) {
        this.password = input.readString().value;
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

com.vmesteonline.be.AuthService_login_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_login_args');
  if (this.email !== null && this.email !== undefined) {
    output.writeFieldBegin('email', Thrift.Type.STRING, 1);
    output.writeString(this.email);
    output.writeFieldEnd();
  }
  if (this.password !== null && this.password !== undefined) {
    output.writeFieldBegin('password', Thrift.Type.STRING, 2);
    output.writeString(this.password);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_login_result = function(args) {
  this.success = null;
  this.exc = null;
  if (args instanceof com.vmesteonline.be.InvalidOperation) {
    this.exc = args;
    return;
  }
  if (args) {
    if (args.success !== undefined) {
      this.success = args.success;
    }
    if (args.exc !== undefined) {
      this.exc = args.exc;
    }
  }
};
com.vmesteonline.be.AuthService_login_result.prototype = {};
com.vmesteonline.be.AuthService_login_result.prototype.read = function(input) {
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
      case 0:
      if (ftype == Thrift.Type.BOOL) {
        this.success = input.readBool().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 1:
      if (ftype == Thrift.Type.STRUCT) {
        this.exc = new com.vmesteonline.be.InvalidOperation();
        this.exc.read(input);
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

com.vmesteonline.be.AuthService_login_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_login_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.BOOL, 0);
    output.writeBool(this.success);
    output.writeFieldEnd();
  }
  if (this.exc !== null && this.exc !== undefined) {
    output.writeFieldBegin('exc', Thrift.Type.STRUCT, 1);
    this.exc.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_registerNewUser_args = function(args) {
  this.firstname = null;
  this.lastname = null;
  this.password = null;
  this.email = null;
  this.locationId = null;
  if (args) {
    if (args.firstname !== undefined) {
      this.firstname = args.firstname;
    }
    if (args.lastname !== undefined) {
      this.lastname = args.lastname;
    }
    if (args.password !== undefined) {
      this.password = args.password;
    }
    if (args.email !== undefined) {
      this.email = args.email;
    }
    if (args.locationId !== undefined) {
      this.locationId = args.locationId;
    }
  }
};
com.vmesteonline.be.AuthService_registerNewUser_args.prototype = {};
com.vmesteonline.be.AuthService_registerNewUser_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRING) {
        this.firstname = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRING) {
        this.lastname = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 3:
      if (ftype == Thrift.Type.STRING) {
        this.password = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 4:
      if (ftype == Thrift.Type.STRING) {
        this.email = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 5:
      if (ftype == Thrift.Type.STRING) {
        this.locationId = input.readString().value;
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

com.vmesteonline.be.AuthService_registerNewUser_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_registerNewUser_args');
  if (this.firstname !== null && this.firstname !== undefined) {
    output.writeFieldBegin('firstname', Thrift.Type.STRING, 1);
    output.writeString(this.firstname);
    output.writeFieldEnd();
  }
  if (this.lastname !== null && this.lastname !== undefined) {
    output.writeFieldBegin('lastname', Thrift.Type.STRING, 2);
    output.writeString(this.lastname);
    output.writeFieldEnd();
  }
  if (this.password !== null && this.password !== undefined) {
    output.writeFieldBegin('password', Thrift.Type.STRING, 3);
    output.writeString(this.password);
    output.writeFieldEnd();
  }
  if (this.email !== null && this.email !== undefined) {
    output.writeFieldBegin('email', Thrift.Type.STRING, 4);
    output.writeString(this.email);
    output.writeFieldEnd();
  }
  if (this.locationId !== null && this.locationId !== undefined) {
    output.writeFieldBegin('locationId', Thrift.Type.STRING, 5);
    output.writeString(this.locationId);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_registerNewUser_result = function(args) {
  this.success = null;
  this.exc = null;
  if (args instanceof com.vmesteonline.be.InvalidOperation) {
    this.exc = args;
    return;
  }
  if (args) {
    if (args.success !== undefined) {
      this.success = args.success;
    }
    if (args.exc !== undefined) {
      this.exc = args.exc;
    }
  }
};
com.vmesteonline.be.AuthService_registerNewUser_result.prototype = {};
com.vmesteonline.be.AuthService_registerNewUser_result.prototype.read = function(input) {
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
      case 0:
      if (ftype == Thrift.Type.I64) {
        this.success = input.readI64().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 1:
      if (ftype == Thrift.Type.STRUCT) {
        this.exc = new com.vmesteonline.be.InvalidOperation();
        this.exc.read(input);
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

com.vmesteonline.be.AuthService_registerNewUser_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_registerNewUser_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.I64, 0);
    output.writeI64(this.success);
    output.writeFieldEnd();
  }
  if (this.exc !== null && this.exc !== undefined) {
    output.writeFieldBegin('exc', Thrift.Type.STRUCT, 1);
    this.exc.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_logout_args = function(args) {
};
com.vmesteonline.be.AuthService_logout_args.prototype = {};
com.vmesteonline.be.AuthService_logout_args.prototype.read = function(input) {
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
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.AuthService_logout_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_logout_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_logout_result = function(args) {
  this.exc = null;
  if (args instanceof com.vmesteonline.be.InvalidOperation) {
    this.exc = args;
    return;
  }
  if (args) {
    if (args.exc !== undefined) {
      this.exc = args.exc;
    }
  }
};
com.vmesteonline.be.AuthService_logout_result.prototype = {};
com.vmesteonline.be.AuthService_logout_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.exc = new com.vmesteonline.be.InvalidOperation();
        this.exc.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.AuthService_logout_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_logout_result');
  if (this.exc !== null && this.exc !== undefined) {
    output.writeFieldBegin('exc', Thrift.Type.STRUCT, 1);
    this.exc.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_setCurrentAttribute_args = function(args) {
  this.typeValueMap = null;
  if (args) {
    if (args.typeValueMap !== undefined) {
      this.typeValueMap = args.typeValueMap;
    }
  }
};
com.vmesteonline.be.AuthService_setCurrentAttribute_args.prototype = {};
com.vmesteonline.be.AuthService_setCurrentAttribute_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.MAP) {
        var _size0 = 0;
        var _rtmp34;
        this.typeValueMap = {};
        var _ktype1 = 0;
        var _vtype2 = 0;
        _rtmp34 = input.readMapBegin();
        _ktype1 = _rtmp34.ktype;
        _vtype2 = _rtmp34.vtype;
        _size0 = _rtmp34.size;
        for (var _i5 = 0; _i5 < _size0; ++_i5)
        {
          if (_i5 > 0 ) {
            if (input.rstack.length > input.rpos[input.rpos.length -1] + 1) {
              input.rstack.pop();
            }
          }
          var key6 = null;
          var val7 = null;
          key6 = input.readI32().value;
          val7 = input.readI64().value;
          this.typeValueMap[key6] = val7;
        }
        input.readMapEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.AuthService_setCurrentAttribute_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_setCurrentAttribute_args');
  if (this.typeValueMap !== null && this.typeValueMap !== undefined) {
    output.writeFieldBegin('typeValueMap', Thrift.Type.MAP, 1);
    output.writeMapBegin(Thrift.Type.I32, Thrift.Type.I64, Thrift.objectLength(this.typeValueMap));
    for (var kiter8 in this.typeValueMap)
    {
      if (this.typeValueMap.hasOwnProperty(kiter8))
      {
        var viter9 = this.typeValueMap[kiter8];
        output.writeI32(kiter8);
        output.writeI64(viter9);
      }
    }
    output.writeMapEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_setCurrentAttribute_result = function(args) {
  this.exc = null;
  if (args instanceof com.vmesteonline.be.InvalidOperation) {
    this.exc = args;
    return;
  }
  if (args) {
    if (args.exc !== undefined) {
      this.exc = args.exc;
    }
  }
};
com.vmesteonline.be.AuthService_setCurrentAttribute_result.prototype = {};
com.vmesteonline.be.AuthService_setCurrentAttribute_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.exc = new com.vmesteonline.be.InvalidOperation();
        this.exc.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.AuthService_setCurrentAttribute_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_setCurrentAttribute_result');
  if (this.exc !== null && this.exc !== undefined) {
    output.writeFieldBegin('exc', Thrift.Type.STRUCT, 1);
    this.exc.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_getCurrentAttributes_args = function(args) {
};
com.vmesteonline.be.AuthService_getCurrentAttributes_args.prototype = {};
com.vmesteonline.be.AuthService_getCurrentAttributes_args.prototype.read = function(input) {
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
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

com.vmesteonline.be.AuthService_getCurrentAttributes_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_getCurrentAttributes_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_getCurrentAttributes_result = function(args) {
  this.success = null;
  this.exc = null;
  if (args instanceof com.vmesteonline.be.InvalidOperation) {
    this.exc = args;
    return;
  }
  if (args) {
    if (args.success !== undefined) {
      this.success = args.success;
    }
    if (args.exc !== undefined) {
      this.exc = args.exc;
    }
  }
};
com.vmesteonline.be.AuthService_getCurrentAttributes_result.prototype = {};
com.vmesteonline.be.AuthService_getCurrentAttributes_result.prototype.read = function(input) {
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
      case 0:
      if (ftype == Thrift.Type.MAP) {
        var _size10 = 0;
        var _rtmp314;
        this.success = {};
        var _ktype11 = 0;
        var _vtype12 = 0;
        _rtmp314 = input.readMapBegin();
        _ktype11 = _rtmp314.ktype;
        _vtype12 = _rtmp314.vtype;
        _size10 = _rtmp314.size;
        for (var _i15 = 0; _i15 < _size10; ++_i15)
        {
          if (_i15 > 0 ) {
            if (input.rstack.length > input.rpos[input.rpos.length -1] + 1) {
              input.rstack.pop();
            }
          }
          var key16 = null;
          var val17 = null;
          key16 = input.readI32().value;
          val17 = input.readI64().value;
          this.success[key16] = val17;
        }
        input.readMapEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 1:
      if (ftype == Thrift.Type.STRUCT) {
        this.exc = new com.vmesteonline.be.InvalidOperation();
        this.exc.read(input);
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

com.vmesteonline.be.AuthService_getCurrentAttributes_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_getCurrentAttributes_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.MAP, 0);
    output.writeMapBegin(Thrift.Type.I32, Thrift.Type.I64, Thrift.objectLength(this.success));
    for (var kiter18 in this.success)
    {
      if (this.success.hasOwnProperty(kiter18))
      {
        var viter19 = this.success[kiter18];
        output.writeI32(kiter18);
        output.writeI64(viter19);
      }
    }
    output.writeMapEnd();
    output.writeFieldEnd();
  }
  if (this.exc !== null && this.exc !== undefined) {
    output.writeFieldBegin('exc', Thrift.Type.STRUCT, 1);
    this.exc.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthServiceClient = function(input, output) {
    this.input = input;
    this.output = (!output) ? input : output;
    this.seqid = 0;
};
com.vmesteonline.be.AuthServiceClient.prototype = {};
com.vmesteonline.be.AuthServiceClient.prototype.login = function(email, password) {
  this.send_login(email, password);
  return this.recv_login();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_login = function(email, password) {
  this.output.writeMessageBegin('login', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_login_args();
  args.email = email;
  args.password = password;
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_login = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new com.vmesteonline.be.AuthService_login_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  if (null !== result.success) {
    return result.success;
  }
  throw 'login failed: unknown result';
};
com.vmesteonline.be.AuthServiceClient.prototype.registerNewUser = function(firstname, lastname, password, email, locationId) {
  this.send_registerNewUser(firstname, lastname, password, email, locationId);
  return this.recv_registerNewUser();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_registerNewUser = function(firstname, lastname, password, email, locationId) {
  this.output.writeMessageBegin('registerNewUser', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_registerNewUser_args();
  args.firstname = firstname;
  args.lastname = lastname;
  args.password = password;
  args.email = email;
  args.locationId = locationId;
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_registerNewUser = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new com.vmesteonline.be.AuthService_registerNewUser_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  if (null !== result.success) {
    return result.success;
  }
  throw 'registerNewUser failed: unknown result';
};
com.vmesteonline.be.AuthServiceClient.prototype.logout = function() {
  this.send_logout();
  this.recv_logout();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_logout = function() {
  this.output.writeMessageBegin('logout', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_logout_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_logout = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new com.vmesteonline.be.AuthService_logout_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  return;
};
com.vmesteonline.be.AuthServiceClient.prototype.setCurrentAttribute = function(typeValueMap) {
  this.send_setCurrentAttribute(typeValueMap);
  this.recv_setCurrentAttribute();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_setCurrentAttribute = function(typeValueMap) {
  this.output.writeMessageBegin('setCurrentAttribute', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_setCurrentAttribute_args();
  args.typeValueMap = typeValueMap;
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_setCurrentAttribute = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new com.vmesteonline.be.AuthService_setCurrentAttribute_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  return;
};
com.vmesteonline.be.AuthServiceClient.prototype.getCurrentAttributes = function() {
  this.send_getCurrentAttributes();
  return this.recv_getCurrentAttributes();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_getCurrentAttributes = function() {
  this.output.writeMessageBegin('getCurrentAttributes', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_getCurrentAttributes_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_getCurrentAttributes = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new com.vmesteonline.be.AuthService_getCurrentAttributes_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  if (null !== result.success) {
    return result.success;
  }
  throw 'getCurrentAttributes failed: unknown result';
};
