package xhj.cn.start;

import zyxhj.utils.Singleton;

public class SingleTest {

	public SingleTest() {
		System.out.println("SingleTest");
	}

	public static class xxx {
		public xxx() {
			System.out.println("xxx");
		}
	}

	public static class yyy {
		public yyy(Integer in, String str) {
			System.out.println(in + " " + str);
		}
	}

	public static void main(String[] args) {
		try {
			SingleTest s1 = Singleton.ins(SingleTest.class);
			SingleTest s2 = Singleton.ins(SingleTest.class);

			SingleTest s3 = Singleton.ins(SingleTest.class);

			xxx x1 = Singleton.ins(xxx.class);
			xxx x2 = Singleton.ins(xxx.class);

			yyy ss1 = Singleton.ins(yyy.class, 123, "sdfh");
			yyy ss2 = Singleton.ins(yyy.class, 1234, "sdsdfsdffh");

			if (s1.equals(s2)) {
				System.out.println("===");
			}

			if (s1.equals(s3)) {
				System.out.println("===");
			}

			if (x1.equals(x2)) {
				System.out.println("===");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
