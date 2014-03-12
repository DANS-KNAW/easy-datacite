/**
 * Validation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.datacontract.schemas._2004._07.Sikb0102_WebService;

public class Validation  implements java.io.Serializable {
    private java.lang.Integer errorCount;

    private org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage[] messages;

    private java.lang.Boolean validXml;

    private java.lang.Integer warningCount;

    public Validation() {
    }

    public Validation(
           java.lang.Integer errorCount,
           org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage[] messages,
           java.lang.Boolean validXml,
           java.lang.Integer warningCount) {
           this.errorCount = errorCount;
           this.messages = messages;
           this.validXml = validXml;
           this.warningCount = warningCount;
    }


    /**
     * Gets the errorCount value for this Validation.
     * 
     * @return errorCount
     */
    public java.lang.Integer getErrorCount() {
        return errorCount;
    }


    /**
     * Sets the errorCount value for this Validation.
     * 
     * @param errorCount
     */
    public void setErrorCount(java.lang.Integer errorCount) {
        this.errorCount = errorCount;
    }


    /**
     * Gets the messages value for this Validation.
     * 
     * @return messages
     */
    public org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage[] getMessages() {
        return messages;
    }


    /**
     * Sets the messages value for this Validation.
     * 
     * @param messages
     */
    public void setMessages(org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage[] messages) {
        this.messages = messages;
    }


    /**
     * Gets the validXml value for this Validation.
     * 
     * @return validXml
     */
    public java.lang.Boolean getValidXml() {
        return validXml;
    }


    /**
     * Sets the validXml value for this Validation.
     * 
     * @param validXml
     */
    public void setValidXml(java.lang.Boolean validXml) {
        this.validXml = validXml;
    }


    /**
     * Gets the warningCount value for this Validation.
     * 
     * @return warningCount
     */
    public java.lang.Integer getWarningCount() {
        return warningCount;
    }


    /**
     * Sets the warningCount value for this Validation.
     * 
     * @param warningCount
     */
    public void setWarningCount(java.lang.Integer warningCount) {
        this.warningCount = warningCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Validation)) return false;
        Validation other = (Validation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errorCount==null && other.getErrorCount()==null) || 
             (this.errorCount!=null &&
              this.errorCount.equals(other.getErrorCount()))) &&
            ((this.messages==null && other.getMessages()==null) || 
             (this.messages!=null &&
              java.util.Arrays.equals(this.messages, other.getMessages()))) &&
            ((this.validXml==null && other.getValidXml()==null) || 
             (this.validXml!=null &&
              this.validXml.equals(other.getValidXml()))) &&
            ((this.warningCount==null && other.getWarningCount()==null) || 
             (this.warningCount!=null &&
              this.warningCount.equals(other.getWarningCount())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getErrorCount() != null) {
            _hashCode += getErrorCount().hashCode();
        }
        if (getMessages() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMessages());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMessages(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getValidXml() != null) {
            _hashCode += getValidXml().hashCode();
        }
        if (getWarningCount() != null) {
            _hashCode += getWarningCount().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Validation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "Validation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "ErrorCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messages");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "Messages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "ValidationMessage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "ValidationMessage"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validXml");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "ValidXml"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warningCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sikb0102.WebService", "WarningCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
