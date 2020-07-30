package org.xujin.janus.damon.serializer;

import org.xujin.janus.damon.exception.JanusCmdException;
import org.xujin.janus.damon.serializer.impl.Hessian1Serializer;
import org.xujin.janus.damon.serializer.impl.HessianSerializer;
import org.xujin.janus.damon.serializer.impl.KryoSerializer;
import org.xujin.janus.damon.serializer.impl.ProtostuffSerializer;

/**
 * @author tbkk
 */
public abstract class AbstractSerializer {
	
	public abstract <T> byte[] serialize(T obj);
	public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

	/**
	 * serializer
	 *
	 *Tips：Enum 是最好的单例方案；枚举单例会初始化全部实现，此处改为托管Class，避免无效的实例化；
	 *
	 * @author tbkk 2019-10-25 21:02:55
	 */
	public enum SerializeEnum {
		HESSIAN(HessianSerializer.class),
		HESSIAN1(Hessian1Serializer.class),
		PROTOSTUFF(ProtostuffSerializer.class),
		KRYO(KryoSerializer.class),
		;

		private Class<? extends AbstractSerializer> serializerClass;
		private SerializeEnum (Class<? extends AbstractSerializer> serializerClass) {
			this.serializerClass = serializerClass;
		}

		public AbstractSerializer getSerializer() {
			try {
				return serializerClass.newInstance();
			} catch (Exception e) {
				throw new JanusCmdException(e);
			}
		}

		public static SerializeEnum match(String name, SerializeEnum defaultSerializer){
			for (SerializeEnum item : SerializeEnum.values()) {
				if (item.name().equals(name)) {
					return item;
				}
			}
			return defaultSerializer;
		}
	}

}
