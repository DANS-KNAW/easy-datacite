package nl.sikb.www.sikb0102.service._1_0_0;

public class ISikb0102ServiceProxy implements nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service {
  private String _endpoint = null;
  private nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service iSikb0102Service = null;
  
  public ISikb0102ServiceProxy() {
    _initISikb0102ServiceProxy();
  }
  
  public ISikb0102ServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initISikb0102ServiceProxy();
  }
  
  private void _initISikb0102ServiceProxy() {
    try {
      iSikb0102Service = (new org.tempuri.Sikb0102ServiceLocator()).getBasicHttpBinding_ISikb0102Service();
      if (iSikb0102Service != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iSikb0102Service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iSikb0102Service)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iSikb0102Service != null)
      ((javax.xml.rpc.Stub)iSikb0102Service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service getISikb0102Service() {
    if (iSikb0102Service == null)
      _initISikb0102ServiceProxy();
    return iSikb0102Service;
  }
  
  public org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse validate(org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlRequest request) throws java.rmi.RemoteException{
    if (iSikb0102Service == null)
      _initISikb0102ServiceProxy();
    return iSikb0102Service.validate(request);
  }
  
  
}