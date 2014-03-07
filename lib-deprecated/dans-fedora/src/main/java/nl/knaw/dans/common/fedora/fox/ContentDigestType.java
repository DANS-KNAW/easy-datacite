package nl.knaw.dans.common.fedora.fox;

public enum ContentDigestType
{
    // @formatter:off
    MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384("SHA-384"), SHA_512("SHA-512"), HAVAL("HAVAL"), TIGER("TIGER"), WHIRLPOOL("WHIRLPOOL"), DISABLED(
            "DISABLED");
    // @formatter:on

    public final String code;

    ContentDigestType(String code)
    {
        this.code = code;
    }

    public static ContentDigestType forCode(final String code)
    {
        return code == null ? null : ContentDigestType.valueOf(code.replaceAll("-", "_"));
    }

}
