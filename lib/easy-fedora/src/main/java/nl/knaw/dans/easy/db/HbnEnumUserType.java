package nl.knaw.dans.easy.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copied from the interwebz: https://www.hibernate.org/265.html
 * 
 * @author vincent ?
 */
public class HbnEnumUserType<E extends Enum<E>> implements UserType {
    private static final Logger log = LoggerFactory.getLogger(HbnEnumUserType.class);
    private Class<E> clazz = null;

    protected HbnEnumUserType(Class<E> c) {
        this.clazz = c;
        log.debug("Instantiated HbnEnumUserType with class {}", c);
    }

    private static final int[] SQL_TYPES = {Types.VARCHAR};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class<E> returnedClass() {
        return clazz;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        String name = resultSet.getString(names[0]);
        E result = null;
        if (!resultSet.wasNull()) {
            result = Enum.valueOf(clazz, name);
        }
        return result;
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        log.debug("Calling nullSafeSet for {}, value = {}, index = {}", this, value, index);
        if (null == value) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            preparedStatement.setString(index, ((Enum) value).name());
        }
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor si, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(resultSet, names, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor si) throws HibernateException, SQLException {
        nullSafeSet(preparedStatement, value, index);
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (null == x || null == y)
            return false;
        return x.equals(y);
    }

    @Override
    public String toString() {
        return String.format("HbnEnumUserType for enum class %s", clazz.getName());
    }

}
