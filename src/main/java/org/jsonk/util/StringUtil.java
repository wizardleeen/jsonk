package org.jsonk.util;

public class StringUtil {

    public static boolean requireEscape(char c) {
        return  c == '\\' || c == '"' || c <= '\u001F';
    }

    public static String escapeChar(char c) {
        return switch (c) {
            case '"' -> "\\\"";
            case '\\' -> "\\\\";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            // All other ASCII control characters (0x00 - 0x1F) must be unicode-escaped
            case '\u0000' -> "\\u0000"; // Null (NUL)
            case '\u0001' -> "\\u0001"; // Start of Heading (SOH)
            case '\u0002' -> "\\u0002"; // Start of Text (STX)
            case '\u0003' -> "\\u0003"; // End of Text (ETX)
            case '\u0004' -> "\\u0004"; // End of Transmission (EOT)
            case '\u0005' -> "\\u0005"; // Enquiry (ENQ)
            case '\u0006' -> "\\u0006"; // Acknowledge (ACK)
            case '\u0007' -> "\\u0007"; // Bell (BEL)
            // 0x08 is \b
            // 0x09 is \t
            // 0x0A is \n
            case '\u000B' -> "\\u000b"; // Vertical Tab (VT)
            // 0x0C is \f
            // 0x0D is \r
            case '\u000E' -> "\\u000e"; // Shift Out (SO)
            case '\u000F' -> "\\u000f"; // Shift In (SI)
            case '\u0010' -> "\\u0010"; // Data Link Escape (DLE)
            case '\u0011' -> "\\u0011"; // Device Control 1 (DC1)
            case '\u0012' -> "\\u0012"; // Device Control 2 (DC2)
            case '\u0013' -> "\\u0013"; // Device Control 3 (DC3)
            case '\u0014' -> "\\u0014"; // Device Control 4 (DC4)
            case '\u0015' -> "\\u0015"; // Negative Acknowledge (NAK)
            case '\u0016' -> "\\u0016"; // Synchronous Idle (SYN)
            case '\u0017' -> "\\u0017"; // End of Transmission Block (ETB)
            case '\u0018' -> "\\u0018"; // Cancel (CAN)
            case '\u0019' -> "\\u0019"; // End of Medium (EM)
            case '\u001A' -> "\\u001a"; // Substitute (SUB)
            case '\u001B' -> "\\u001b"; // Escape (ESC)
            case '\u001C' -> "\\u001c"; // File Separator (FS)
            case '\u001D' -> "\\u001d"; // Group Separator (GS)
            case '\u001E' -> "\\u001e"; // Record Separator (RS)
            case '\u001F' -> "\\u001f"; // Unit Separator (US)
            default -> Character.toString(c);
        };
    }
    
    public static String decapitalize(String str) {
        if (str.isEmpty())
            return str;
        if (str.length() > 1 && Character.isUpperCase(str.charAt(1)))
            return str;
        var chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }


    public static String escape(String s) {
        var sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(escapeChar(s.charAt(i)));
        }
        return sb.toString();
    }

    public static int escape(char[] buf, int start, int length) {
        var newLen = length;
        for (int i = 0; i < length; i++) {
            var c = buf[start + i];
            if (c == '\t' || c == '\n' || c == '"' || c == '\\')
                newLen++;
            else if (c <= '\u001F')
                newLen += 5;
        }
        assert start + newLen < buf.length;
        var r1 = start + newLen - 1;
        var r2 = start + length - 1;
        for (int i = start + length - 1; i >= start; i--) {
            var c = buf[i];
            var shifts = r1 - r2;
            var segLen = r2 - i;
            var k = i + shifts;
            switch (c) {
                case '\t' -> {
                    assert shifts >= 1;
                    rightShift(buf, i + 1, shifts, segLen);
                    buf[k - 1] = '\\';
                    buf[k] = 't';
                    r1 = k - 2;
                    r2 = i - 1;
                }
                case '\n' -> {
                    assert shifts >= 1;
                    rightShift(buf, i + 1, shifts, segLen);
                    buf[k - 1] = '\\';
                    buf[k] = 'n';
                    r1 = k - 2;
                    r2 = i - 1;
                }
                case '"' -> {
                    assert shifts >= 1;
                    rightShift(buf, i + 1, shifts, segLen);
                    buf[k - 1] = '\\';
                    buf[k] = '"';
                    r1 = k - 2;
                    r2 = i - 1;
                }
                case '\\' -> {
                    assert shifts >= 1;
                    rightShift(buf, i + 1, shifts, segLen);
                    buf[k - 1] = '\\';
                    buf[k] = '\\';
                    r1 = k - 2;
                    r2 = i - 1;
                }
                default -> {
                    if (c <= '\u001F') {
                        assert shifts >= 5;
                        rightShift(buf, i + 1, shifts, segLen);
                        buf[k - 5] = '\\';
                        buf[k - 4] = 'u';
                        buf[k - 3] = '0';
                        buf[k - 2] = '0';
                        var d = (int) c;
                        buf[k - 1] = toHex(d >> 4 & 0xf);
                        buf[k] = toHex(d & 0xf);
                        r1 = k - 6;
                        r2 = i - 1;
                    }
                }
            }
        }
        return newLen;
    }

    static char toHex(int i) {
        assert i <= 0xf;
        if (i < 10)
            return (char) ('0' + i);
        else
            return (char) ('A' + i - 10);
    }

    private static void rightShift(char[] buf, int offset, int shifts, int len) {
        System.arraycopy(buf, offset, buf, offset + shifts, len);
    }

}
