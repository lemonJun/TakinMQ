package com.bj58.esb.server.store;

import java.util.HashMap;
import java.util.Map;

public class DeletePolicyFactory {
    private static Map<String/* name */, Class<? extends DeletePolicy>> policyMap =
            new HashMap<String, Class<? extends DeletePolicy>>();
    static {
        DeletePolicyFactory.registerDeletePolicy(DiscardDeletePolicy.NAME, DiscardDeletePolicy.class);
        DeletePolicyFactory.registerDeletePolicy(ArchiveDeletePolicy.NAME, ArchiveDeletePolicy.class);
    }


    public static void registerDeletePolicy(String name, Class<? extends DeletePolicy> clazz) {
        policyMap.put(name, clazz);
    }


    public static DeletePolicy getDeletePolicy(String values) {
        String[] tmps = values.split(",");
        String name = tmps[0];
        Class<? extends DeletePolicy> clazz = policyMap.get(name);
        if (clazz == null) {
//            throw new UnknownDeletePolicyException(name);
            try {
				throw new Exception(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        try {
            DeletePolicy deletePolicy = clazz.newInstance();
            String[] initValues = null;
            if (tmps.length >= 2) {
                initValues = new String[tmps.length - 1];
                System.arraycopy(tmps, 1, initValues, 0, tmps.length - 1);
            }
            deletePolicy.init(initValues);
            return deletePolicy;
        }
        catch (Exception e) {
//            throw new MetamorphosisServerStartupException("New delete policy `" + name + "` failed", e);
            try {
				throw new Exception("New delete policy `" + name + "` failed", e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        }
		return null;

    }
}