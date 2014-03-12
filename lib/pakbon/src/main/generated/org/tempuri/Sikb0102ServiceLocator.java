/**
 * Sikb0102ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Sikb0102ServiceLocator extends org.apache.axis.client.Service implements org.tempuri.Sikb0102Service {

    public Sikb0102ServiceLocator() {
    }


    public Sikb0102ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Sikb0102ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_ISikb0102Service
    private java.lang.String BasicHttpBinding_ISikb0102Service_address = "https://tools.sikb.nl/sikb0102service/Sikb0102Service.svc";

    public java.lang.String getBasicHttpBinding_ISikb0102ServiceAddress() {
        return BasicHttpBinding_ISikb0102Service_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_ISikb0102ServiceWSDDServiceName = "BasicHttpBinding_ISikb0102Service";

    public java.lang.String getBasicHttpBinding_ISikb0102ServiceWSDDServiceName() {
        return BasicHttpBinding_ISikb0102ServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_ISikb0102ServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_ISikb0102ServiceWSDDServiceName = name;
    }

    public nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service getBasicHttpBinding_ISikb0102Service() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_ISikb0102Service_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_ISikb0102Service(endpoint);
    }

    public nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service getBasicHttpBinding_ISikb0102Service(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.BasicHttpBinding_ISikb0102ServiceStub _stub = new org.tempuri.BasicHttpBinding_ISikb0102ServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_ISikb0102ServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_ISikb0102ServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_ISikb0102Service_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (nl.sikb.www.sikb0102.service._1_0_0.ISikb0102Service.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.BasicHttpBinding_ISikb0102ServiceStub _stub = new org.tempuri.BasicHttpBinding_ISikb0102ServiceStub(new java.net.URL(BasicHttpBinding_ISikb0102Service_address), this);
                _stub.setPortName(getBasicHttpBinding_ISikb0102ServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_ISikb0102Service".equals(inputPortName)) {
            return getBasicHttpBinding_ISikb0102Service();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "Sikb0102Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_ISikb0102Service"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_ISikb0102Service".equals(portName)) {
            setBasicHttpBinding_ISikb0102ServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
