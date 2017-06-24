Ext.apply( Ext.lib.Ajax ,
{ forceActiveX:false,
  createXhrObject:function(transactionId)
        {
            var obj,http;
            try
            {
            	
		if(Ext.isIE7 && !!this.forceActiveX){throw("IE7forceActiveX");}
		
                http = new XMLHttpRequest();

                obj = { conn:http, tId:transactionId };
            }
            catch(e)
            {
                for (var i = 0; i < this.activeX.length; ++i) {
                    try
                    {

                        http = new ActiveXObject(this.activeX[i]);

                        obj = { conn:http, tId:transactionId };
                        break;
                    }
                    catch(e) {
                    }
                }
            }
            finally
            {
                return obj;
            }
        },
        getHttpStatus: function(reqObj){
        
        	var statObj = {  status:0
        			,statusText:''
        			,isError:false
        			,isLocal:false
        			,isOK:false };
        	try {
        		if(!reqObj)throw('noobj');
		        statObj.status = reqObj.status || 0;
		        
		        statObj.isLocal = !reqObj.status && location.protocol == "file:" || 
		        		   Ext.isSafari && reqObj.status == undefined;
		        	
		        statObj.statusText = reqObj.statusText || ''; 
		        
		        statObj.isOK = (statObj.isLocal || 
		        		(statObj.status > 199 && statObj.status < 300) ||
		        		 statObj.status == 304);
		        
		    } catch(e){ statObj.isError = true;} //status may not avail/valid yet.
		    
    		return statObj; 
        
        },
        handleTransactionResponse:function(o, callback, isAbort)
		{

		var responseObject;
		
		callback = callback || {};
		
		o.status = this.getHttpStatus(o.conn);
				
		 if(!o.status.isError){
		 	/* create and enhance the response with proper status and XMLDOM if necessary */
		 	responseObject = this.createResponseObject(o, callback.argument);
		 }
			 
		 if(o.status.isError){ /* checked again in case exception was raised - ActiveX was disabled during XML-DOM creation? */
		   responseObject = this.createExceptionObject(o.tId, callback.argument, (isAbort ? isAbort : false));
		 }

		 if (o.status.isOK && !o.status.isError) {
			if (callback.success) {
				if (!callback.scope) {
					callback.success(responseObject);
				}
				else {
					callback.success.apply(callback.scope, [responseObject]);
				}
			}
		  } else {

			if (callback.failure) {
				if (!callback.scope) {
					callback.failure(responseObject);
				}
				else {
					callback.failure.apply(callback.scope, [responseObject]);
				}
			}

		 }

		this.releaseObject(o);
		responseObject = null;
	},
	createResponseObject:function(o, callbackArg)
	        {
	            var obj = {};
	            var headerObj = {};
	
	            try
	            {
	                var headerStr = o.conn.getAllResponseHeaders();
	                var header = headerStr.split('\n');
	                for (var i = 0; i < header.length; i++) {
	                    var delimitPos = header[i].indexOf(':');
	                    if (delimitPos != -1) {
	                        headerObj[header[i].substring(0, delimitPos)] = header[i].substring(delimitPos + 2);
	                    }
	                }
	            }
	            catch(e) {
	            }
	
	            obj.tId = o.tId;
	            obj.status = o.status.status;
	            obj.statusText = o.status.statusText;
	            obj.getResponseHeader = headerObj;
	            obj.getAllResponseHeaders = headerStr;
	            obj.responseText = o.conn.responseText;
	            obj.responseXML = o.conn.responseXML;

	            if(o.status.isLocal){
	            	   
	            	   o.status.isOK = ((obj.status = o.status.status = (!!obj.responseText.length)?200:404) == 200);
	            	   
	            	   if(o.status.isOK && (!obj.responseXML || obj.responseXML.childNodes.length == 0)){

				var xdoc=null;
				try{   //ActiveX may be disabled
					if(typeof(DOMParser) == 'undefined'){ 
						xdoc=new ActiveXObject("Microsoft.XMLDOM"); 
						xdoc.async="false";
						xdoc.loadXML(obj.responseText); 
					}else{ 
						var domParser = new DOMParser(); 
						xdoc = domParser.parseFromString(obj.responseText, 'application/xml'); 
						domParser = null;
					}
				} catch(ex){ 
					o.status.isError = true; 
					
					}

				obj.responseXML = xdoc;

				if ( xdoc && typeof (obj.getResponseHeader['Content-Type']) == 'undefined' && 
					!!xdoc.childNodes.length )    /* Got valid nodes? then set the response header */
					{
						obj.getResponseHeader['Content-Type'] == 'text/xml';

					}

			   }
			   
		    }	
	            
		     
	            if (typeof callbackArg !== undefined) {
	                obj.argument = callbackArg;
	            }
	
	            return obj;
        },

        asyncRequest:function(method, uri, callback, postData)
        {
            var o = this.getConnectionObject();

            if (!o) {
                return null;
            }
            else {
                try{
			o.conn.open(method, uri, true);
		} catch(ex){
			
			this.handleTransactionResponse(o, callback);
			return o;
		}

		if (this.useDefaultXhrHeader) {
		    if (!this.defaultHeaders['X-Requested-With']) {
			this.initHeader('X-Requested-With', this.defaultXhrHeader, true);
		    }
		}

		if(postData && this.useDefaultHeader){
		    this.initHeader('Content-Type', this.defaultPostHeader);
		}

		 if (this.hasDefaultHeaders || this.hasHeaders) {
		    this.setHeader(o);
		}

		this.handleReadyState(o, callback);
		
		try{ o.conn.send(postData || null);
		} catch(ex){ this.handleTransactionResponse(o, callback);}
			
		return o;
            }
        }});
	
	Ext.lib.Ajax.forceActiveX = (document.location.protocol == 'file:');/* or other true/false mechanism */