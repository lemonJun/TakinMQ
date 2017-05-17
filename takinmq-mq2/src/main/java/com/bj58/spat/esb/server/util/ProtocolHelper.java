package com.bj58.spat.esb.server.util;

import com.bj58.spat.esb.server.protocol.ProtocolConst;

public class ProtocolHelper {

    /**
     * 获得协义的版本号
     * @param buffer
     * @return
     */
    public static int getVersion(byte[] buffer) {
        return buffer[0];
    }

    /**
     * 解析协义
     * @param buffer
     * @return
     * @throws Exception
     */
    public static Object fromBytes(byte[] buffer) throws Exception {
        if (buffer != null && buffer.length > 0) {
            int version = buffer[0];
            //			if(version == com.bj58.spat.scf.protocol.sfp.v1.Protocol.VERSION) {
            //				return com.bj58.spat.scf.protocol.sfp.v1.Protocol.fromBytes(buffer);
            //			}
        }

        throw new Exception("不完整的二进制流");
    }

    /**
     * 
     * @param buf
     * @return
     */
    public static boolean checkHeadDelimiter(byte[] buf) {
        if (buf.length == ProtocolConst.P_START_TAG.length) {
            for (int i = 0; i < buf.length; i++) {
                if (buf[i] != ProtocolConst.P_START_TAG[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
