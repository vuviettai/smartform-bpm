package com.smartform.customize.fnt;

public class Status {
	public static enum Packing {
		INITED(1),
		SHIPPED(2),
		STORED(3),
		DELIVERING(4),
		DELIVERED(5);

		public boolean equals(Integer value) {
			return this.value == value;
		}

		private Integer value;

		private Packing(Integer value) {
			// TODO Auto-generated constructor stub
			this.value = value;
		}

		public Integer toValue() {
			return value;
		}

		public static Packing fromValue(Integer value) throws IllegalArgumentException {
			for (final Packing elm : Packing.values()) {
				if (elm.equals(value))
					return elm;
			}
			throw new IllegalArgumentException("Invalid value " + value);
		}
	}
	public enum Issue {
		OPENED("opened"),
		CLOSED("closed");

		public boolean equals(String val) {
			return String.valueOf(this.status) == val;
		}

		private String status;

		private Issue(String status) {
			// TODO Auto-generated constructor stub
			this.status = status;
		}

		@Override
		public String toString() {
			return String.valueOf(status);
		}

		public String getValue() {
			return this.status;
		}

		public static Issue fromString(String literal) throws IllegalArgumentException {
			for (final Issue type : Issue.values()) {
				if (type.equals(literal))
					return type;
			}
			throw new IllegalArgumentException("Invalid literal " + literal);
		}
	}
	public enum Store {
		UNSTORED(0),
		ARRIVED(1),
		NORMAL(2),
		ABNORMAL(4),
		READY(8),
		DELIVERED(16);

		public boolean equals(Integer val) {
			return this.status == val;
		}

		public boolean equals(String val) {
			return String.valueOf(this.status) == val;
		}

		private int status;

		private Store(Integer status) {
			// TODO Auto-generated constructor stub
			this.status = status;
		}

		@Override
		public String toString() {
			return String.valueOf(status);
		}

		public Integer getValue() {
			return this.status;
		}

		public static Store fromString(String literal) throws IllegalArgumentException {
			for (final Store type : Store.values()) {
				if (type.equals(literal))
					return type;
			}
			throw new IllegalArgumentException("Invalid literal " + literal);
		}
	}

	public static enum Shipment {
		INITED("inited"),
		PARTIALLY_SHIPPING("partiallyshipping"),
		FULLY_SHIPPING("fulltshipping"),
		PARTIALLY_STORED("partiallystored"),
		FULLY_STORED("fullystored");

		public boolean equals(String op) {
			return this.type.equalsIgnoreCase(op);
		}

		private String type;

		private Shipment(String type) {
			// TODO Auto-generated constructor stub
			this.type = type;
		}

		@Override
		public String toString() {
			return type;
		}

		public static Shipment fromString(String literal) throws IllegalArgumentException {
			for (final Shipment type : Shipment.values()) {
				if (type.equals(literal))
					return type;
			}
			throw new IllegalArgumentException("Invalid literal " + literal);
		}
	}
}
