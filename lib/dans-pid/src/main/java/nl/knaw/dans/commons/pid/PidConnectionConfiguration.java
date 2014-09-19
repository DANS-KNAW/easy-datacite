package nl.knaw.dans.commons.pid;

public class PidConnectionConfiguration {
    private String username;
    private String password;
    private String connectionUrl;
    private String driverClass;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    /**
     * @param driverClass
     *        Note that Maven needs dependencies to support specific databases, see also {@link PidTableProperties}.
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
}
