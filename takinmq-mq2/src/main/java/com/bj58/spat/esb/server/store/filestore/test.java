package com.bj58.spat.esb.server.store.filestore;

public class test {
	public static void main(String[] args) throws Exception {
		FSQueue fq = new FSQueue("D:/work/common");
		String str = "hello world1234233512d;nfslkdnvlsa;djfoisdklnlksdjflsjdoifnskdnfisdfsldkfjs!!!";
		long start = System.currentTimeMillis();
		for (int i = 0; i < 612000; i++) {
			fq.add(str);	
			byte[] b = fq.readNextAndRemove();
//			String msg = new String(b, "UTF-8");
		//	System.out.println("queue length: " + fq.getQueuSize() + " message: "+msg);
		}
		long end = System.currentTimeMillis();
		long value = end - start;
		System.out.println(value + "ms");
	//	byte[][] b = fq.readnMesg(611900);
	//	System.out.println("queue length: " + fq.getQueuSize() + "b length: " + b.length);

		/*	for (int i = 0; i < 511900; i++) {
			String msg = new String(b[i], "UTF-8");
			System.out.println("queue length: " + fq.getQueuSize() + "message: " + msg + " " +i);
		}*/
		
/*		System.out.println("queue length: "+fq.getQueuSize());
		fq.readGoBack(35260);
		System.out.println("queue length end: "+fq.getQueuSize());
		for (int j = 0; j < 400; j++) {
			byte[] b = fq.readNextAndRemove();
			if (b == null) {
				System.out.println("b is null");
			}
			String msg = new String(b, "UTF-8");
			System.out.println("queue length: " + fq.getQueuSize() + " message: "+msg);
		}*/
		fq.close();
		System.out.println("END");
	}

}
