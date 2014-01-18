//
// Autogenerated by Thrift Compiler (0.9.1)
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
      if (ftype == Thrift.Type.STRUCT) {
        this.success = new com.vmesteonline.be.Session();
        this.success.read(input);
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
    output.writeFieldBegin('success', Thrift.Type.STRUCT, 0);
    this.success.write(output);
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

com.vmesteonline.be.AuthService_getSession_args = function(args) {
  this.salt = null;
  if (args) {
    if (args.salt !== undefined) {
      this.salt = args.salt;
    }
  }
};
com.vmesteonline.be.AuthService_getSession_args.prototype = {};
com.vmesteonline.be.AuthService_getSession_args.prototype.read = function(input) {
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
        this.salt = input.readString().value;
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

com.vmesteonline.be.AuthService_getSession_args.prototype.write = function(output) {
  output.writeStructBegin('AuthService_getSession_args');
  if (this.salt !== null && this.salt !== undefined) {
    output.writeFieldBegin('salt', Thrift.Type.STRING, 1);
    output.writeString(this.salt);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

com.vmesteonline.be.AuthService_getSession_result = function(args) {
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
com.vmesteonline.be.AuthService_getSession_result.prototype = {};
com.vmesteonline.be.AuthService_getSession_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.success = new com.vmesteonline.be.Session();
        this.success.read(input);
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

com.vmesteonline.be.AuthService_getSession_result.prototype.write = function(output) {
  output.writeStructBegin('AuthService_getSession_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.STRUCT, 0);
    this.success.write(output);
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
  this.uname = null;
  this.password = null;
  this.groupId = null;
  this.email = null;
  if (args) {
    if (args.uname !== undefined) {
      this.uname = args.uname;
    }
    if (args.password !== undefined) {
      this.password = args.password;
    }
    if (args.groupId !== undefined) {
      this.groupId = args.groupId;
    }
    if (args.email !== undefined) {
      this.email = args.email;
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
        this.uname = input.readString().value;
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
      case 3:
      if (ftype == Thrift.Type.I64) {
        this.groupId = input.readI64().value;
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
  if (this.uname !== null && this.uname !== undefined) {
    output.writeFieldBegin('uname', Thrift.Type.STRING, 1);
    output.writeString(this.uname);
    output.writeFieldEnd();
  }
  if (this.password !== null && this.password !== undefined) {
    output.writeFieldBegin('password', Thrift.Type.STRING, 2);
    output.writeString(this.password);
    output.writeFieldEnd();
  }
  if (this.groupId !== null && this.groupId !== undefined) {
    output.writeFieldBegin('groupId', Thrift.Type.I64, 3);
    output.writeI64(this.groupId);
    output.writeFieldEnd();
  }
  if (this.email !== null && this.email !== undefined) {
    output.writeFieldBegin('email', Thrift.Type.STRING, 4);
    output.writeString(this.email);
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
      if (ftype == Thrift.Type.I32) {
        this.success = input.readI32().value;
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
    output.writeFieldBegin('success', Thrift.Type.I32, 0);
    output.writeI32(this.success);
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
com.vmesteonline.be.AuthServiceClient.prototype.getSession = function(salt) {
  this.send_getSession(salt);
  return this.recv_getSession();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_getSession = function(salt) {
  this.output.writeMessageBegin('getSession', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_getSession_args();
  args.salt = salt;
  args.write(this.output);
  this.output.writeMessageEnd();
  return this.output.getTransport().flush();
};

com.vmesteonline.be.AuthServiceClient.prototype.recv_getSession = function() {
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
  var result = new com.vmesteonline.be.AuthService_getSession_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.exc) {
    throw result.exc;
  }
  if (null !== result.success) {
    return result.success;
  }
  throw 'getSession failed: unknown result';
};
com.vmesteonline.be.AuthServiceClient.prototype.registerNewUser = function(uname, password, groupId, email) {
  this.send_registerNewUser(uname, password, groupId, email);
  return this.recv_registerNewUser();
};

com.vmesteonline.be.AuthServiceClient.prototype.send_registerNewUser = function(uname, password, groupId, email) {
  this.output.writeMessageBegin('registerNewUser', Thrift.MessageType.CALL, this.seqid);
  var args = new com.vmesteonline.be.AuthService_registerNewUser_args();
  args.uname = uname;
  args.password = password;
  args.groupId = groupId;
  args.email = email;
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
