"undefined"==typeof com&&(com={}),"undefined"==typeof com.vmesteonline&&(com.vmesteonline={}),"undefined"==typeof com.vmesteonline.be&&(com.vmesteonline.be={}),"undefined"==typeof com.vmesteonline.be.shop&&(com.vmesteonline.be.shop={}),com.vmesteonline.be.shop.DateType={CLEAN:0,NEXT_ORDER:1,SPECIAL_PRICE:2,CLOSED:3},com.vmesteonline.be.shop.DeliveryType={UNKNOWN:0,SELF_PICKUP:1,SHORT_RANGE:2,LONG_RANGE:3},com.vmesteonline.be.shop.PaymentType={UNKNOWN:0,CASH:1,CREDIT_CARD:2,TRANSFER:3,SHOP_CREDIT:5},com.vmesteonline.be.shop.PriceType={RETAIL:0,INET:1,VIP:2,SPECIAL:3},com.vmesteonline.be.shop.OrderDatesType={ORDER_WEEKLY:0,ORDER_MOUNTHLY:1},com.vmesteonline.be.shop.OrderStatus={UNKNOWN:0,NEW:1,CONFIRMED:2,SHIPPING:3,DELIVERED:4,CLOSED:5,CANCELED:6},com.vmesteonline.be.shop.PaymentStatus={UNKNOWN:0,WAIT:1,PENDING:2,COMPLETE:3,CREDIT:4},com.vmesteonline.be.shop.Shop=function(a){this.id=null,this.name=null,this.descr=null,this.address=null,this.logoURL=null,this.ownerId=null,this.topicSet=null,this.tags=null,this.deliveryCosts=null,this.paymentTypes=null,this.deliveryByWeightIncrement=null,this.deliveryCostByDistance=null,this.deliveryTypeAddressMasks=null,a&&(void 0!==a.id&&(this.id=a.id),void 0!==a.name&&(this.name=a.name),void 0!==a.descr&&(this.descr=a.descr),void 0!==a.address&&(this.address=a.address),void 0!==a.logoURL&&(this.logoURL=a.logoURL),void 0!==a.ownerId&&(this.ownerId=a.ownerId),void 0!==a.topicSet&&(this.topicSet=a.topicSet),void 0!==a.tags&&(this.tags=a.tags),void 0!==a.deliveryCosts&&(this.deliveryCosts=a.deliveryCosts),void 0!==a.paymentTypes&&(this.paymentTypes=a.paymentTypes),void 0!==a.deliveryByWeightIncrement&&(this.deliveryByWeightIncrement=a.deliveryByWeightIncrement),void 0!==a.deliveryCostByDistance&&(this.deliveryCostByDistance=a.deliveryCostByDistance),void 0!==a.deliveryTypeAddressMasks&&(this.deliveryTypeAddressMasks=a.deliveryTypeAddressMasks))},com.vmesteonline.be.shop.Shop.prototype={},com.vmesteonline.be.shop.Shop.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I64?this.id=a.readI64().value:a.skip(c);break;case 2:c==Thrift.Type.STRING?this.name=a.readString().value:a.skip(c);break;case 3:c==Thrift.Type.STRING?this.descr=a.readString().value:a.skip(c);break;case 4:c==Thrift.Type.STRUCT?(this.address=new com.vmesteonline.be.PostalAddress,this.address.read(a)):a.skip(c);break;case 5:c==Thrift.Type.STRING?this.logoURL=a.readString().value:a.skip(c);break;case 6:c==Thrift.Type.I64?this.ownerId=a.readI64().value:a.skip(c);break;case 7:if(c==Thrift.Type.LIST){var e,f=0;this.topicSet=[];var g=0;e=a.readListBegin(),g=e.etype,f=e.size;for(var h=0;f>h;++h){var i=null;i=a.readI64().value,this.topicSet.push(i)}a.readListEnd()}else a.skip(c);break;case 8:if(c==Thrift.Type.LIST){var j,k=0;this.tags=[];var l=0;j=a.readListBegin(),l=j.etype,k=j.size;for(var m=0;k>m;++m){var n=null;n=a.readString().value,this.tags.push(n)}a.readListEnd()}else a.skip(c);break;case 9:if(c==Thrift.Type.MAP){var o,p=0;this.deliveryCosts={};var q=0,r=0;o=a.readMapBegin(),q=o.ktype,r=o.vtype,p=o.size;for(var s=0;p>s;++s){s>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var t=null,u=null;t=a.readI32().value,u=a.readDouble().value,this.deliveryCosts[t]=u}a.readMapEnd()}else a.skip(c);break;case 10:if(c==Thrift.Type.MAP){var v,w=0;this.paymentTypes={};var x=0,y=0;v=a.readMapBegin(),x=v.ktype,y=v.vtype,w=v.size;for(var z=0;w>z;++z){z>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var A=null,B=null;A=a.readI32().value,B=a.readDouble().value,this.paymentTypes[A]=B}a.readMapEnd()}else a.skip(c);break;case 11:if(c==Thrift.Type.MAP){var C,D=0;this.deliveryByWeightIncrement={};var E=0,F=0;C=a.readMapBegin(),E=C.ktype,F=C.vtype,D=C.size;for(var G=0;D>G;++G){G>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var H=null,I=null;H=a.readI32().value,I=a.readI32().value,this.deliveryByWeightIncrement[H]=I}a.readMapEnd()}else a.skip(c);break;case 12:if(c==Thrift.Type.MAP){var J,K=0;this.deliveryCostByDistance={};var L=0,M=0;J=a.readMapBegin(),L=J.ktype,M=J.vtype,K=J.size;for(var N=0;K>N;++N){N>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var O=null,P=null;O=a.readI32().value,P=a.readDouble().value,this.deliveryCostByDistance[O]=P}a.readMapEnd()}else a.skip(c);break;case 13:if(c==Thrift.Type.MAP){var Q,R=0;this.deliveryTypeAddressMasks={};var S=0,T=0;Q=a.readMapBegin(),S=Q.ktype,T=Q.vtype,R=Q.size;for(var U=0;R>U;++U){U>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var V=null,W=null;V=a.readI32().value,W=a.readString().value,this.deliveryTypeAddressMasks[V]=W}a.readMapEnd()}else a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.Shop.prototype.write=function(a){if(a.writeStructBegin("Shop"),null!==this.id&&void 0!==this.id&&(a.writeFieldBegin("id",Thrift.Type.I64,1),a.writeI64(this.id),a.writeFieldEnd()),null!==this.name&&void 0!==this.name&&(a.writeFieldBegin("name",Thrift.Type.STRING,2),a.writeString(this.name),a.writeFieldEnd()),null!==this.descr&&void 0!==this.descr&&(a.writeFieldBegin("descr",Thrift.Type.STRING,3),a.writeString(this.descr),a.writeFieldEnd()),null!==this.address&&void 0!==this.address&&(a.writeFieldBegin("address",Thrift.Type.STRUCT,4),this.address.write(a),a.writeFieldEnd()),null!==this.logoURL&&void 0!==this.logoURL&&(a.writeFieldBegin("logoURL",Thrift.Type.STRING,5),a.writeString(this.logoURL),a.writeFieldEnd()),null!==this.ownerId&&void 0!==this.ownerId&&(a.writeFieldBegin("ownerId",Thrift.Type.I64,6),a.writeI64(this.ownerId),a.writeFieldEnd()),null!==this.topicSet&&void 0!==this.topicSet){a.writeFieldBegin("topicSet",Thrift.Type.LIST,7),a.writeListBegin(Thrift.Type.I64,this.topicSet.length);for(var b in this.topicSet)this.topicSet.hasOwnProperty(b)&&(b=this.topicSet[b],a.writeI64(b));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.tags&&void 0!==this.tags){a.writeFieldBegin("tags",Thrift.Type.LIST,8),a.writeListBegin(Thrift.Type.STRING,this.tags.length);for(var c in this.tags)this.tags.hasOwnProperty(c)&&(c=this.tags[c],a.writeString(c));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.deliveryCosts&&void 0!==this.deliveryCosts){a.writeFieldBegin("deliveryCosts",Thrift.Type.MAP,9),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.DOUBLE,Thrift.objectLength(this.deliveryCosts));for(var d in this.deliveryCosts)if(this.deliveryCosts.hasOwnProperty(d)){var e=this.deliveryCosts[d];a.writeI32(d),a.writeDouble(e)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.paymentTypes&&void 0!==this.paymentTypes){a.writeFieldBegin("paymentTypes",Thrift.Type.MAP,10),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.DOUBLE,Thrift.objectLength(this.paymentTypes));for(var f in this.paymentTypes)if(this.paymentTypes.hasOwnProperty(f)){var g=this.paymentTypes[f];a.writeI32(f),a.writeDouble(g)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.deliveryByWeightIncrement&&void 0!==this.deliveryByWeightIncrement){a.writeFieldBegin("deliveryByWeightIncrement",Thrift.Type.MAP,11),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.I32,Thrift.objectLength(this.deliveryByWeightIncrement));for(var h in this.deliveryByWeightIncrement)if(this.deliveryByWeightIncrement.hasOwnProperty(h)){var i=this.deliveryByWeightIncrement[h];a.writeI32(h),a.writeI32(i)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.deliveryCostByDistance&&void 0!==this.deliveryCostByDistance){a.writeFieldBegin("deliveryCostByDistance",Thrift.Type.MAP,12),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.DOUBLE,Thrift.objectLength(this.deliveryCostByDistance));for(var j in this.deliveryCostByDistance)if(this.deliveryCostByDistance.hasOwnProperty(j)){var k=this.deliveryCostByDistance[j];a.writeI32(j),a.writeDouble(k)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.deliveryTypeAddressMasks&&void 0!==this.deliveryTypeAddressMasks){a.writeFieldBegin("deliveryTypeAddressMasks",Thrift.Type.MAP,13),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.STRING,Thrift.objectLength(this.deliveryTypeAddressMasks));for(var l in this.deliveryTypeAddressMasks)if(this.deliveryTypeAddressMasks.hasOwnProperty(l)){var m=this.deliveryTypeAddressMasks[l];a.writeI32(l),a.writeString(m)}a.writeMapEnd(),a.writeFieldEnd()}a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.Producer=function(a){this.id=null,this.name=null,this.descr=null,this.logoURL=null,this.homeURL=null,a&&(void 0!==a.id&&(this.id=a.id),void 0!==a.name&&(this.name=a.name),void 0!==a.descr&&(this.descr=a.descr),void 0!==a.logoURL&&(this.logoURL=a.logoURL),void 0!==a.homeURL&&(this.homeURL=a.homeURL))},com.vmesteonline.be.shop.Producer.prototype={},com.vmesteonline.be.shop.Producer.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I64?this.id=a.readI64().value:a.skip(c);break;case 2:c==Thrift.Type.STRING?this.name=a.readString().value:a.skip(c);break;case 3:c==Thrift.Type.STRING?this.descr=a.readString().value:a.skip(c);break;case 4:c==Thrift.Type.STRING?this.logoURL=a.readString().value:a.skip(c);break;case 5:c==Thrift.Type.STRING?this.homeURL=a.readString().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.Producer.prototype.write=function(a){a.writeStructBegin("Producer"),null!==this.id&&void 0!==this.id&&(a.writeFieldBegin("id",Thrift.Type.I64,1),a.writeI64(this.id),a.writeFieldEnd()),null!==this.name&&void 0!==this.name&&(a.writeFieldBegin("name",Thrift.Type.STRING,2),a.writeString(this.name),a.writeFieldEnd()),null!==this.descr&&void 0!==this.descr&&(a.writeFieldBegin("descr",Thrift.Type.STRING,3),a.writeString(this.descr),a.writeFieldEnd()),null!==this.logoURL&&void 0!==this.logoURL&&(a.writeFieldBegin("logoURL",Thrift.Type.STRING,4),a.writeString(this.logoURL),a.writeFieldEnd()),null!==this.homeURL&&void 0!==this.homeURL&&(a.writeFieldBegin("homeURL",Thrift.Type.STRING,5),a.writeString(this.homeURL),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.ProductCategory=function(a){this.id=null,this.parentId=null,this.name=null,this.descr=null,this.logoURLset=null,this.topicSet=null,this.productCount=null,a&&(void 0!==a.id&&(this.id=a.id),void 0!==a.parentId&&(this.parentId=a.parentId),void 0!==a.name&&(this.name=a.name),void 0!==a.descr&&(this.descr=a.descr),void 0!==a.logoURLset&&(this.logoURLset=a.logoURLset),void 0!==a.topicSet&&(this.topicSet=a.topicSet),void 0!==a.productCount&&(this.productCount=a.productCount))},com.vmesteonline.be.shop.ProductCategory.prototype={},com.vmesteonline.be.shop.ProductCategory.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I64?this.id=a.readI64().value:a.skip(c);break;case 2:c==Thrift.Type.I64?this.parentId=a.readI64().value:a.skip(c);break;case 3:c==Thrift.Type.STRING?this.name=a.readString().value:a.skip(c);break;case 4:c==Thrift.Type.STRING?this.descr=a.readString().value:a.skip(c);break;case 5:if(c==Thrift.Type.LIST){var e,f=0;this.logoURLset=[];var g=0;e=a.readListBegin(),g=e.etype,f=e.size;for(var h=0;f>h;++h){var i=null;i=a.readString().value,this.logoURLset.push(i)}a.readListEnd()}else a.skip(c);break;case 6:if(c==Thrift.Type.LIST){var j,k=0;this.topicSet=[];var l=0;j=a.readListBegin(),l=j.etype,k=j.size;for(var m=0;k>m;++m){var n=null;n=a.readI64().value,this.topicSet.push(n)}a.readListEnd()}else a.skip(c);break;case 7:c==Thrift.Type.I32?this.productCount=a.readI32().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.ProductCategory.prototype.write=function(a){if(a.writeStructBegin("ProductCategory"),null!==this.id&&void 0!==this.id&&(a.writeFieldBegin("id",Thrift.Type.I64,1),a.writeI64(this.id),a.writeFieldEnd()),null!==this.parentId&&void 0!==this.parentId&&(a.writeFieldBegin("parentId",Thrift.Type.I64,2),a.writeI64(this.parentId),a.writeFieldEnd()),null!==this.name&&void 0!==this.name&&(a.writeFieldBegin("name",Thrift.Type.STRING,3),a.writeString(this.name),a.writeFieldEnd()),null!==this.descr&&void 0!==this.descr&&(a.writeFieldBegin("descr",Thrift.Type.STRING,4),a.writeString(this.descr),a.writeFieldEnd()),null!==this.logoURLset&&void 0!==this.logoURLset){a.writeFieldBegin("logoURLset",Thrift.Type.LIST,5),a.writeListBegin(Thrift.Type.STRING,this.logoURLset.length);for(var b in this.logoURLset)this.logoURLset.hasOwnProperty(b)&&(b=this.logoURLset[b],a.writeString(b));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.topicSet&&void 0!==this.topicSet){a.writeFieldBegin("topicSet",Thrift.Type.LIST,6),a.writeListBegin(Thrift.Type.I64,this.topicSet.length);for(var c in this.topicSet)this.topicSet.hasOwnProperty(c)&&(c=this.topicSet[c],a.writeI64(c));a.writeListEnd(),a.writeFieldEnd()}null!==this.productCount&&void 0!==this.productCount&&(a.writeFieldBegin("productCount",Thrift.Type.I32,7),a.writeI32(this.productCount),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.ProductDetails=function(a){this.categories=null,this.fullDescr=null,this.imagesURLset=null,this.pricesMap=null,this.optionsMap=null,this.topicSet=null,this.producerId=null,this.minProducerPack=null,this.knownNames=null,a&&(void 0!==a.categories&&(this.categories=a.categories),void 0!==a.fullDescr&&(this.fullDescr=a.fullDescr),void 0!==a.imagesURLset&&(this.imagesURLset=a.imagesURLset),void 0!==a.pricesMap&&(this.pricesMap=a.pricesMap),void 0!==a.optionsMap&&(this.optionsMap=a.optionsMap),void 0!==a.topicSet&&(this.topicSet=a.topicSet),void 0!==a.producerId&&(this.producerId=a.producerId),void 0!==a.minProducerPack&&(this.minProducerPack=a.minProducerPack),void 0!==a.knownNames&&(this.knownNames=a.knownNames))},com.vmesteonline.be.shop.ProductDetails.prototype={},com.vmesteonline.be.shop.ProductDetails.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:if(c==Thrift.Type.LIST){var e,f=0;this.categories=[];var g=0;e=a.readListBegin(),g=e.etype,f=e.size;for(var h=0;f>h;++h){var i=null;i=a.readI64().value,this.categories.push(i)}a.readListEnd()}else a.skip(c);break;case 2:c==Thrift.Type.STRING?this.fullDescr=a.readString().value:a.skip(c);break;case 3:if(c==Thrift.Type.LIST){var j,k=0;this.imagesURLset=[];var l=0;j=a.readListBegin(),l=j.etype,k=j.size;for(var m=0;k>m;++m){var n=null;n=a.readString().value,this.imagesURLset.push(n)}a.readListEnd()}else a.skip(c);break;case 4:if(c==Thrift.Type.MAP){var o,p=0;this.pricesMap={};var q=0,r=0;o=a.readMapBegin(),q=o.ktype,r=o.vtype,p=o.size;for(var s=0;p>s;++s){s>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var t=null,u=null;t=a.readI32().value,u=a.readDouble().value,this.pricesMap[t]=u}a.readMapEnd()}else a.skip(c);break;case 5:if(c==Thrift.Type.MAP){var v,w=0;this.optionsMap={};var x=0,y=0;v=a.readMapBegin(),x=v.ktype,y=v.vtype,w=v.size;for(var z=0;w>z;++z){z>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var A=null,B=null;A=a.readString().value,B=a.readString().value,this.optionsMap[A]=B}a.readMapEnd()}else a.skip(c);break;case 6:if(c==Thrift.Type.LIST){var C,D=0;this.topicSet=[];var E=0;C=a.readListBegin(),E=C.etype,D=C.size;for(var F=0;D>F;++F){var G=null;G=a.readI64().value,this.topicSet.push(G)}a.readListEnd()}else a.skip(c);break;case 7:c==Thrift.Type.I64?this.producerId=a.readI64().value:a.skip(c);break;case 9:c==Thrift.Type.DOUBLE?this.minProducerPack=a.readDouble().value:a.skip(c);break;case 10:if(c==Thrift.Type.SET){var H,I=0;this.knownNames=[];var J=0;H=a.readSetBegin(),J=H.etype,I=H.size;for(var K=0;I>K;++K){var L=null;L=a.readString().value,this.knownNames.push(L)}a.readSetEnd()}else a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.ProductDetails.prototype.write=function(a){if(a.writeStructBegin("ProductDetails"),null!==this.categories&&void 0!==this.categories){a.writeFieldBegin("categories",Thrift.Type.LIST,1),a.writeListBegin(Thrift.Type.I64,this.categories.length);for(var b in this.categories)this.categories.hasOwnProperty(b)&&(b=this.categories[b],a.writeI64(b));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.fullDescr&&void 0!==this.fullDescr&&(a.writeFieldBegin("fullDescr",Thrift.Type.STRING,2),a.writeString(this.fullDescr),a.writeFieldEnd()),null!==this.imagesURLset&&void 0!==this.imagesURLset){a.writeFieldBegin("imagesURLset",Thrift.Type.LIST,3),a.writeListBegin(Thrift.Type.STRING,this.imagesURLset.length);for(var c in this.imagesURLset)this.imagesURLset.hasOwnProperty(c)&&(c=this.imagesURLset[c],a.writeString(c));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.pricesMap&&void 0!==this.pricesMap){a.writeFieldBegin("pricesMap",Thrift.Type.MAP,4),a.writeMapBegin(Thrift.Type.I32,Thrift.Type.DOUBLE,Thrift.objectLength(this.pricesMap));for(var d in this.pricesMap)if(this.pricesMap.hasOwnProperty(d)){var e=this.pricesMap[d];a.writeI32(d),a.writeDouble(e)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.optionsMap&&void 0!==this.optionsMap){a.writeFieldBegin("optionsMap",Thrift.Type.MAP,5),a.writeMapBegin(Thrift.Type.STRING,Thrift.Type.STRING,Thrift.objectLength(this.optionsMap));for(var f in this.optionsMap)if(this.optionsMap.hasOwnProperty(f)){var g=this.optionsMap[f];a.writeString(f),a.writeString(g)}a.writeMapEnd(),a.writeFieldEnd()}if(null!==this.topicSet&&void 0!==this.topicSet){a.writeFieldBegin("topicSet",Thrift.Type.LIST,6),a.writeListBegin(Thrift.Type.I64,this.topicSet.length);for(var h in this.topicSet)this.topicSet.hasOwnProperty(h)&&(h=this.topicSet[h],a.writeI64(h));a.writeListEnd(),a.writeFieldEnd()}if(null!==this.producerId&&void 0!==this.producerId&&(a.writeFieldBegin("producerId",Thrift.Type.I64,7),a.writeI64(this.producerId),a.writeFieldEnd()),null!==this.minProducerPack&&void 0!==this.minProducerPack&&(a.writeFieldBegin("minProducerPack",Thrift.Type.DOUBLE,9),a.writeDouble(this.minProducerPack),a.writeFieldEnd()),null!==this.knownNames&&void 0!==this.knownNames){a.writeFieldBegin("knownNames",Thrift.Type.SET,10),a.writeSetBegin(Thrift.Type.STRING,this.knownNames.length);for(var i in this.knownNames)this.knownNames.hasOwnProperty(i)&&(i=this.knownNames[i],a.writeString(i));a.writeSetEnd(),a.writeFieldEnd()}a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.Product=function(a){this.id=null,this.name=null,this.shortDescr=null,this.weight=null,this.imageURL=null,this.price=null,this.unitName=null,this.minClientPack=null,this.shopId=null,this.prepackRequired=null,a&&(void 0!==a.id&&(this.id=a.id),void 0!==a.name&&(this.name=a.name),void 0!==a.shortDescr&&(this.shortDescr=a.shortDescr),void 0!==a.weight&&(this.weight=a.weight),void 0!==a.imageURL&&(this.imageURL=a.imageURL),void 0!==a.price&&(this.price=a.price),void 0!==a.unitName&&(this.unitName=a.unitName),void 0!==a.minClientPack&&(this.minClientPack=a.minClientPack),void 0!==a.shopId&&(this.shopId=a.shopId),void 0!==a.prepackRequired&&(this.prepackRequired=a.prepackRequired))},com.vmesteonline.be.shop.Product.prototype={},com.vmesteonline.be.shop.Product.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I64?this.id=a.readI64().value:a.skip(c);break;case 2:c==Thrift.Type.STRING?this.name=a.readString().value:a.skip(c);break;case 3:c==Thrift.Type.STRING?this.shortDescr=a.readString().value:a.skip(c);break;case 4:c==Thrift.Type.DOUBLE?this.weight=a.readDouble().value:a.skip(c);break;case 5:c==Thrift.Type.STRING?this.imageURL=a.readString().value:a.skip(c);break;case 6:c==Thrift.Type.DOUBLE?this.price=a.readDouble().value:a.skip(c);break;case 7:c==Thrift.Type.STRING?this.unitName=a.readString().value:a.skip(c);break;case 8:c==Thrift.Type.DOUBLE?this.minClientPack=a.readDouble().value:a.skip(c);break;case 9:c==Thrift.Type.I64?this.shopId=a.readI64().value:a.skip(c);break;case 10:c==Thrift.Type.BOOL?this.prepackRequired=a.readBool().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.Product.prototype.write=function(a){a.writeStructBegin("Product"),null!==this.id&&void 0!==this.id&&(a.writeFieldBegin("id",Thrift.Type.I64,1),a.writeI64(this.id),a.writeFieldEnd()),null!==this.name&&void 0!==this.name&&(a.writeFieldBegin("name",Thrift.Type.STRING,2),a.writeString(this.name),a.writeFieldEnd()),null!==this.shortDescr&&void 0!==this.shortDescr&&(a.writeFieldBegin("shortDescr",Thrift.Type.STRING,3),a.writeString(this.shortDescr),a.writeFieldEnd()),null!==this.weight&&void 0!==this.weight&&(a.writeFieldBegin("weight",Thrift.Type.DOUBLE,4),a.writeDouble(this.weight),a.writeFieldEnd()),null!==this.imageURL&&void 0!==this.imageURL&&(a.writeFieldBegin("imageURL",Thrift.Type.STRING,5),a.writeString(this.imageURL),a.writeFieldEnd()),null!==this.price&&void 0!==this.price&&(a.writeFieldBegin("price",Thrift.Type.DOUBLE,6),a.writeDouble(this.price),a.writeFieldEnd()),null!==this.unitName&&void 0!==this.unitName&&(a.writeFieldBegin("unitName",Thrift.Type.STRING,7),a.writeString(this.unitName),a.writeFieldEnd()),null!==this.minClientPack&&void 0!==this.minClientPack&&(a.writeFieldBegin("minClientPack",Thrift.Type.DOUBLE,8),a.writeDouble(this.minClientPack),a.writeFieldEnd()),null!==this.shopId&&void 0!==this.shopId&&(a.writeFieldBegin("shopId",Thrift.Type.I64,9),a.writeI64(this.shopId),a.writeFieldEnd()),null!==this.prepackRequired&&void 0!==this.prepackRequired&&(a.writeFieldBegin("prepackRequired",Thrift.Type.BOOL,10),a.writeBool(this.prepackRequired),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.FullProductInfo=function(a){this.product=null,this.details=null,a&&(void 0!==a.product&&(this.product=a.product),void 0!==a.details&&(this.details=a.details))},com.vmesteonline.be.shop.FullProductInfo.prototype={},com.vmesteonline.be.shop.FullProductInfo.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.STRUCT?(this.product=new com.vmesteonline.be.shop.Product,this.product.read(a)):a.skip(c);break;case 2:c==Thrift.Type.STRUCT?(this.details=new com.vmesteonline.be.shop.ProductDetails,this.details.read(a)):a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.FullProductInfo.prototype.write=function(a){a.writeStructBegin("FullProductInfo"),null!==this.product&&void 0!==this.product&&(a.writeFieldBegin("product",Thrift.Type.STRUCT,1),this.product.write(a),a.writeFieldEnd()),null!==this.details&&void 0!==this.details&&(a.writeFieldBegin("details",Thrift.Type.STRUCT,2),this.details.write(a),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.OrderDates=function(a){this.type=null,this.orderDay=null,this.orderBefore=null,this.eachOddEven=null,this.priceTypeToUse=null,a&&(void 0!==a.type&&(this.type=a.type),void 0!==a.orderDay&&(this.orderDay=a.orderDay),void 0!==a.orderBefore&&(this.orderBefore=a.orderBefore),void 0!==a.eachOddEven&&(this.eachOddEven=a.eachOddEven),void 0!==a.priceTypeToUse&&(this.priceTypeToUse=a.priceTypeToUse))},com.vmesteonline.be.shop.OrderDates.prototype={},com.vmesteonline.be.shop.OrderDates.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I32?this.type=a.readI32().value:a.skip(c);break;case 2:c==Thrift.Type.I32?this.orderDay=a.readI32().value:a.skip(c);break;case 3:c==Thrift.Type.I32?this.orderBefore=a.readI32().value:a.skip(c);break;case 4:c==Thrift.Type.I32?this.eachOddEven=a.readI32().value:a.skip(c);break;case 5:c==Thrift.Type.I32?this.priceTypeToUse=a.readI32().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.OrderDates.prototype.write=function(a){a.writeStructBegin("OrderDates"),null!==this.type&&void 0!==this.type&&(a.writeFieldBegin("type",Thrift.Type.I32,1),a.writeI32(this.type),a.writeFieldEnd()),null!==this.orderDay&&void 0!==this.orderDay&&(a.writeFieldBegin("orderDay",Thrift.Type.I32,2),a.writeI32(this.orderDay),a.writeFieldEnd()),null!==this.orderBefore&&void 0!==this.orderBefore&&(a.writeFieldBegin("orderBefore",Thrift.Type.I32,3),a.writeI32(this.orderBefore),a.writeFieldEnd()),null!==this.eachOddEven&&void 0!==this.eachOddEven&&(a.writeFieldBegin("eachOddEven",Thrift.Type.I32,4),a.writeI32(this.eachOddEven),a.writeFieldEnd()),null!==this.priceTypeToUse&&void 0!==this.priceTypeToUse&&(a.writeFieldBegin("priceTypeToUse",Thrift.Type.I32,5),a.writeI32(this.priceTypeToUse),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.OrderDate=function(a){this.orderDate=null,this.priceType=null,a&&(void 0!==a.orderDate&&(this.orderDate=a.orderDate),void 0!==a.priceType&&(this.priceType=a.priceType))},com.vmesteonline.be.shop.OrderDate.prototype={},com.vmesteonline.be.shop.OrderDate.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I32?this.orderDate=a.readI32().value:a.skip(c);break;case 2:c==Thrift.Type.I32?this.priceType=a.readI32().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.OrderDate.prototype.write=function(a){a.writeStructBegin("OrderDate"),null!==this.orderDate&&void 0!==this.orderDate&&(a.writeFieldBegin("orderDate",Thrift.Type.I32,1),a.writeI32(this.orderDate),a.writeFieldEnd()),null!==this.priceType&&void 0!==this.priceType&&(a.writeFieldBegin("priceType",Thrift.Type.I32,2),a.writeI32(this.priceType),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.OrderLine=function(a){this.product=null,this.quantity=null,this.price=null,this.packs=null,this.comment=null,a&&(void 0!==a.product&&(this.product=a.product),void 0!==a.quantity&&(this.quantity=a.quantity),void 0!==a.price&&(this.price=a.price),void 0!==a.packs&&(this.packs=a.packs),void 0!==a.comment&&(this.comment=a.comment))},com.vmesteonline.be.shop.OrderLine.prototype={},com.vmesteonline.be.shop.OrderLine.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.STRUCT?(this.product=new com.vmesteonline.be.shop.Product,this.product.read(a)):a.skip(c);break;case 2:c==Thrift.Type.DOUBLE?this.quantity=a.readDouble().value:a.skip(c);break;case 3:c==Thrift.Type.DOUBLE?this.price=a.readDouble().value:a.skip(c);break;case 4:if(c==Thrift.Type.MAP){var e,f=0;this.packs={};var g=0,h=0;e=a.readMapBegin(),g=e.ktype,h=e.vtype,f=e.size;for(var i=0;f>i;++i){i>0&&a.rstack.length>a.rpos[a.rpos.length-1]+1&&a.rstack.pop();var j=null,k=null;j=a.readDouble().value,k=a.readI32().value,this.packs[j]=k}a.readMapEnd()}else a.skip(c);break;case 5:c==Thrift.Type.STRING?this.comment=a.readString().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.OrderLine.prototype.write=function(a){if(a.writeStructBegin("OrderLine"),null!==this.product&&void 0!==this.product&&(a.writeFieldBegin("product",Thrift.Type.STRUCT,1),this.product.write(a),a.writeFieldEnd()),null!==this.quantity&&void 0!==this.quantity&&(a.writeFieldBegin("quantity",Thrift.Type.DOUBLE,2),a.writeDouble(this.quantity),a.writeFieldEnd()),null!==this.price&&void 0!==this.price&&(a.writeFieldBegin("price",Thrift.Type.DOUBLE,3),a.writeDouble(this.price),a.writeFieldEnd()),null!==this.packs&&void 0!==this.packs){a.writeFieldBegin("packs",Thrift.Type.MAP,4),a.writeMapBegin(Thrift.Type.DOUBLE,Thrift.Type.I32,Thrift.objectLength(this.packs));for(var b in this.packs)if(this.packs.hasOwnProperty(b)){var c=this.packs[b];a.writeDouble(b),a.writeI32(c)}a.writeMapEnd(),a.writeFieldEnd()}null!==this.comment&&void 0!==this.comment&&(a.writeFieldBegin("comment",Thrift.Type.STRING,5),a.writeString(this.comment),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.OrderDetails=function(a){this.createdAt=null,this.delivery=null,this.deliveryCost=null,this.deliveryTo=null,this.paymentType=null,this.paymentStatus=null,this.odrerLines=null,this.comment=null,this.weightGramm=null,a&&(void 0!==a.createdAt&&(this.createdAt=a.createdAt),void 0!==a.delivery&&(this.delivery=a.delivery),void 0!==a.deliveryCost&&(this.deliveryCost=a.deliveryCost),void 0!==a.deliveryTo&&(this.deliveryTo=a.deliveryTo),void 0!==a.paymentType&&(this.paymentType=a.paymentType),void 0!==a.paymentStatus&&(this.paymentStatus=a.paymentStatus),void 0!==a.odrerLines&&(this.odrerLines=a.odrerLines),void 0!==a.comment&&(this.comment=a.comment),void 0!==a.weightGramm&&(this.weightGramm=a.weightGramm))},com.vmesteonline.be.shop.OrderDetails.prototype={},com.vmesteonline.be.shop.OrderDetails.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I32?this.createdAt=a.readI32().value:a.skip(c);break;case 2:c==Thrift.Type.I32?this.delivery=a.readI32().value:a.skip(c);break;case 3:c==Thrift.Type.DOUBLE?this.deliveryCost=a.readDouble().value:a.skip(c);break;case 4:c==Thrift.Type.STRUCT?(this.deliveryTo=new com.vmesteonline.be.PostalAddress,this.deliveryTo.read(a)):a.skip(c);break;case 5:c==Thrift.Type.I32?this.paymentType=a.readI32().value:a.skip(c);break;case 6:c==Thrift.Type.I32?this.paymentStatus=a.readI32().value:a.skip(c);break;case 7:if(c==Thrift.Type.LIST){var e,f=0;this.odrerLines=[];var g=0;e=a.readListBegin(),g=e.etype,f=e.size;for(var h=0;f>h;++h){var i=null;i=new com.vmesteonline.be.shop.OrderLine,i.read(a),this.odrerLines.push(i)}a.readListEnd()}else a.skip(c);break;case 8:c==Thrift.Type.STRING?this.comment=a.readString().value:a.skip(c);break;case 9:c==Thrift.Type.I32?this.weightGramm=a.readI32().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.OrderDetails.prototype.write=function(a){if(a.writeStructBegin("OrderDetails"),null!==this.createdAt&&void 0!==this.createdAt&&(a.writeFieldBegin("createdAt",Thrift.Type.I32,1),a.writeI32(this.createdAt),a.writeFieldEnd()),null!==this.delivery&&void 0!==this.delivery&&(a.writeFieldBegin("delivery",Thrift.Type.I32,2),a.writeI32(this.delivery),a.writeFieldEnd()),null!==this.deliveryCost&&void 0!==this.deliveryCost&&(a.writeFieldBegin("deliveryCost",Thrift.Type.DOUBLE,3),a.writeDouble(this.deliveryCost),a.writeFieldEnd()),null!==this.deliveryTo&&void 0!==this.deliveryTo&&(a.writeFieldBegin("deliveryTo",Thrift.Type.STRUCT,4),this.deliveryTo.write(a),a.writeFieldEnd()),null!==this.paymentType&&void 0!==this.paymentType&&(a.writeFieldBegin("paymentType",Thrift.Type.I32,5),a.writeI32(this.paymentType),a.writeFieldEnd()),null!==this.paymentStatus&&void 0!==this.paymentStatus&&(a.writeFieldBegin("paymentStatus",Thrift.Type.I32,6),a.writeI32(this.paymentStatus),a.writeFieldEnd()),null!==this.odrerLines&&void 0!==this.odrerLines){a.writeFieldBegin("odrerLines",Thrift.Type.LIST,7),a.writeListBegin(Thrift.Type.STRUCT,this.odrerLines.length);for(var b in this.odrerLines)this.odrerLines.hasOwnProperty(b)&&(b=this.odrerLines[b],b.write(a));a.writeListEnd(),a.writeFieldEnd()}null!==this.comment&&void 0!==this.comment&&(a.writeFieldBegin("comment",Thrift.Type.STRING,8),a.writeString(this.comment),a.writeFieldEnd()),null!==this.weightGramm&&void 0!==this.weightGramm&&(a.writeFieldBegin("weightGramm",Thrift.Type.I32,9),a.writeI32(this.weightGramm),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.Order=function(a){this.id=null,this.date=null,this.status=null,this.priceType=null,this.totalCost=null,this.userId=null,this.userName=null,a&&(void 0!==a.id&&(this.id=a.id),void 0!==a.date&&(this.date=a.date),void 0!==a.status&&(this.status=a.status),void 0!==a.priceType&&(this.priceType=a.priceType),void 0!==a.totalCost&&(this.totalCost=a.totalCost),void 0!==a.userId&&(this.userId=a.userId),void 0!==a.userName&&(this.userName=a.userName))
},com.vmesteonline.be.shop.Order.prototype={},com.vmesteonline.be.shop.Order.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.I64?this.id=a.readI64().value:a.skip(c);break;case 2:c==Thrift.Type.I32?this.date=a.readI32().value:a.skip(c);break;case 3:c==Thrift.Type.I32?this.status=a.readI32().value:a.skip(c);break;case 4:c==Thrift.Type.I32?this.priceType=a.readI32().value:a.skip(c);break;case 5:c==Thrift.Type.DOUBLE?this.totalCost=a.readDouble().value:a.skip(c);break;case 6:c==Thrift.Type.I64?this.userId=a.readI64().value:a.skip(c);break;case 7:c==Thrift.Type.STRING?this.userName=a.readString().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.Order.prototype.write=function(a){a.writeStructBegin("Order"),null!==this.id&&void 0!==this.id&&(a.writeFieldBegin("id",Thrift.Type.I64,1),a.writeI64(this.id),a.writeFieldEnd()),null!==this.date&&void 0!==this.date&&(a.writeFieldBegin("date",Thrift.Type.I32,2),a.writeI32(this.date),a.writeFieldEnd()),null!==this.status&&void 0!==this.status&&(a.writeFieldBegin("status",Thrift.Type.I32,3),a.writeI32(this.status),a.writeFieldEnd()),null!==this.priceType&&void 0!==this.priceType&&(a.writeFieldBegin("priceType",Thrift.Type.I32,4),a.writeI32(this.priceType),a.writeFieldEnd()),null!==this.totalCost&&void 0!==this.totalCost&&(a.writeFieldBegin("totalCost",Thrift.Type.DOUBLE,5),a.writeDouble(this.totalCost),a.writeFieldEnd()),null!==this.userId&&void 0!==this.userId&&(a.writeFieldBegin("userId",Thrift.Type.I64,6),a.writeI64(this.userId),a.writeFieldEnd()),null!==this.userName&&void 0!==this.userName&&(a.writeFieldBegin("userName",Thrift.Type.STRING,7),a.writeString(this.userName),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.FullOrder=function(a){this.order=null,this.details=null,a&&(void 0!==a.order&&(this.order=a.order),void 0!==a.details&&(this.details=a.details))},com.vmesteonline.be.shop.FullOrder.prototype={},com.vmesteonline.be.shop.FullOrder.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.STRUCT?(this.order=new com.vmesteonline.be.shop.Order,this.order.read(a)):a.skip(c);break;case 2:c==Thrift.Type.STRUCT?(this.details=new com.vmesteonline.be.shop.OrderDetails,this.details.read(a)):a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.FullOrder.prototype.write=function(a){a.writeStructBegin("FullOrder"),null!==this.order&&void 0!==this.order&&(a.writeFieldBegin("order",Thrift.Type.STRUCT,1),this.order.write(a),a.writeFieldEnd()),null!==this.details&&void 0!==this.details&&(a.writeFieldBegin("details",Thrift.Type.STRUCT,2),this.details.write(a),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.ProductListPart=function(a){this.products=null,this.length=null,a&&(void 0!==a.products&&(this.products=a.products),void 0!==a.length&&(this.length=a.length))},com.vmesteonline.be.shop.ProductListPart.prototype={},com.vmesteonline.be.shop.ProductListPart.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:if(c==Thrift.Type.LIST){var e,f=0;this.products=[];var g=0;e=a.readListBegin(),g=e.etype,f=e.size;for(var h=0;f>h;++h){var i=null;i=new com.vmesteonline.be.shop.Product,i.read(a),this.products.push(i)}a.readListEnd()}else a.skip(c);break;case 2:c==Thrift.Type.I32?this.length=a.readI32().value:a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.ProductListPart.prototype.write=function(a){if(a.writeStructBegin("ProductListPart"),null!==this.products&&void 0!==this.products){a.writeFieldBegin("products",Thrift.Type.LIST,1),a.writeListBegin(Thrift.Type.STRUCT,this.products.length);for(var b in this.products)this.products.hasOwnProperty(b)&&(b=this.products[b],b.write(a));a.writeListEnd(),a.writeFieldEnd()}null!==this.length&&void 0!==this.length&&(a.writeFieldBegin("length",Thrift.Type.I32,2),a.writeI32(this.length),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()},com.vmesteonline.be.shop.OrderUpdateInfo=function(a){this.totalCost=null,this.delivery=null,this.deliveryCost=null,this.weightGramm=null,this.newOrderLine=null,a&&(void 0!==a.totalCost&&(this.totalCost=a.totalCost),void 0!==a.delivery&&(this.delivery=a.delivery),void 0!==a.deliveryCost&&(this.deliveryCost=a.deliveryCost),void 0!==a.weightGramm&&(this.weightGramm=a.weightGramm),void 0!==a.newOrderLine&&(this.newOrderLine=a.newOrderLine))},com.vmesteonline.be.shop.OrderUpdateInfo.prototype={},com.vmesteonline.be.shop.OrderUpdateInfo.prototype.read=function(a){for(a.readStructBegin();;){var b=a.readFieldBegin(),c=(b.fname,b.ftype),d=b.fid;if(c==Thrift.Type.STOP)break;switch(d){case 1:c==Thrift.Type.DOUBLE?this.totalCost=a.readDouble().value:a.skip(c);break;case 2:c==Thrift.Type.I32?this.delivery=a.readI32().value:a.skip(c);break;case 3:c==Thrift.Type.DOUBLE?this.deliveryCost=a.readDouble().value:a.skip(c);break;case 4:c==Thrift.Type.I32?this.weightGramm=a.readI32().value:a.skip(c);break;case 5:c==Thrift.Type.STRUCT?(this.newOrderLine=new com.vmesteonline.be.shop.OrderLine,this.newOrderLine.read(a)):a.skip(c);break;default:a.skip(c)}a.readFieldEnd()}a.readStructEnd()},com.vmesteonline.be.shop.OrderUpdateInfo.prototype.write=function(a){a.writeStructBegin("OrderUpdateInfo"),null!==this.totalCost&&void 0!==this.totalCost&&(a.writeFieldBegin("totalCost",Thrift.Type.DOUBLE,1),a.writeDouble(this.totalCost),a.writeFieldEnd()),null!==this.delivery&&void 0!==this.delivery&&(a.writeFieldBegin("delivery",Thrift.Type.I32,2),a.writeI32(this.delivery),a.writeFieldEnd()),null!==this.deliveryCost&&void 0!==this.deliveryCost&&(a.writeFieldBegin("deliveryCost",Thrift.Type.DOUBLE,3),a.writeDouble(this.deliveryCost),a.writeFieldEnd()),null!==this.weightGramm&&void 0!==this.weightGramm&&(a.writeFieldBegin("weightGramm",Thrift.Type.I32,4),a.writeI32(this.weightGramm),a.writeFieldEnd()),null!==this.newOrderLine&&void 0!==this.newOrderLine&&(a.writeFieldBegin("newOrderLine",Thrift.Type.STRUCT,5),this.newOrderLine.write(a),a.writeFieldEnd()),a.writeFieldStop(),a.writeStructEnd()};