package com.box.common.core.json.gson.extension

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


operator fun JsonObject.getValue(thisRef: Any?, property: KProperty<*>): JsonElement = obj[property.name]

operator fun JsonObject.setValue(thisRef: Any?, property: KProperty<*>, value: JsonElement) { obj[property.name] = value }


class JsonObjectDelegate<T : Any>(private val _obj: JsonObject, private val _get: (JsonElement) -> T, private val _set: (T) -> JsonElement, private val _key: String? = null, private val _default: (() -> T)? = null) : ReadWriteProperty<Any?, T> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val element = _obj[_key ?: property.name]
        if(element === null) {
            val default = _default
            if (default === null)
                throw NoSuchElementException("'$_key' not found")
            else
                return default.invoke()
        }
        return _get(element)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        _obj[_key ?: property.name] = _set(value)
    }

}

class NullableJsonObjectDelegate<T: Any?>(private val _obj: JsonObject, private val _get: (JsonElement) -> T?, private val _set: (T?) -> JsonElement, private val _key: String? = null, private val _default: (() -> T)? = null) : ReadWriteProperty<Any?, T?> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val element = _obj[_key ?: property.name]
        if (element === null)
            return _default?.invoke()
        return _get(element)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        _obj[_key ?: property.name] = _set(value)
    }

}

class JsonArrayDelegate<T: Any>(private val _array: JsonArray, private val _index: Int, private val _get: (JsonElement) -> T, private val _set: (T) -> JsonElement) : ReadWriteProperty<Any?, T> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = _get(_array[_index])

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { _array[_index] = _set(value) }

}

val JsonElement.byString     : JsonObjectDelegate<String> get() = JsonObjectDelegate(this.obj, { it.string }, { it.toJsonPrimitive() })
val JsonElement.byBool       : JsonObjectDelegate<Boolean> get() = JsonObjectDelegate(this.obj, { it.bool }, { it.toJsonPrimitive() })
val JsonElement.byByte       : JsonObjectDelegate<Byte> get() = JsonObjectDelegate(this.obj, { it.byte }, { it.toJsonPrimitive() })
val JsonElement.byChar       : JsonObjectDelegate<Char> get() = JsonObjectDelegate(this.obj, { it.char }, { it.toJsonPrimitive() })
val JsonElement.byShort      : JsonObjectDelegate<Short> get() = JsonObjectDelegate(this.obj, { it.short }, { it.toJsonPrimitive() })
val JsonElement.byInt        : JsonObjectDelegate<Int> get() = JsonObjectDelegate(this.obj, { it.int }, { it.toJsonPrimitive() })
val JsonElement.byLong       : JsonObjectDelegate<Long> get() = JsonObjectDelegate(this.obj, { it.long }, { it.toJsonPrimitive() })
val JsonElement.byFloat      : JsonObjectDelegate<Float> get() = JsonObjectDelegate(this.obj, { it.float }, { it.toJsonPrimitive() })
val JsonElement.byDouble     : JsonObjectDelegate<Double> get() = JsonObjectDelegate(this.obj, { it.double }, { it.toJsonPrimitive() })
val JsonElement.byNumber     : JsonObjectDelegate<Number> get() = JsonObjectDelegate(this.obj, { it.number }, { it.toJsonPrimitive() })
val JsonElement.byBigInteger : JsonObjectDelegate<BigInteger> get() = JsonObjectDelegate(this.obj, { it.bigInteger }, { it.toJsonPrimitive() })
val JsonElement.byBigDecimal : JsonObjectDelegate<BigDecimal> get() = JsonObjectDelegate(this.obj, { it.bigDecimal }, { it.toJsonPrimitive() })
val JsonElement.byArray      : JsonObjectDelegate<JsonArray> get() = JsonObjectDelegate(this.obj, { it.array }, { it })
val JsonElement.byObject     : JsonObjectDelegate<JsonObject> get() = JsonObjectDelegate(this.obj, { it.obj }, { it })

fun JsonElement.byString     ( key: String? = null, default: ( () -> String     )? = null ) : JsonObjectDelegate<String> = JsonObjectDelegate(this.obj, { it.string }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byBool       ( key: String? = null, default: ( () -> Boolean    )? = null ) : JsonObjectDelegate<Boolean> = JsonObjectDelegate(this.obj, { it.bool }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byByte       ( key: String? = null, default: ( () -> Byte       )? = null ) : JsonObjectDelegate<Byte> = JsonObjectDelegate(this.obj, { it.byte }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byChar       ( key: String? = null, default: ( () -> Char       )? = null ) : JsonObjectDelegate<Char> = JsonObjectDelegate(this.obj, { it.char }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byShort      ( key: String? = null, default: ( () -> Short      )? = null ) : JsonObjectDelegate<Short> = JsonObjectDelegate(this.obj, { it.short }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byInt        ( key: String? = null, default: ( () -> Int        )? = null ) : JsonObjectDelegate<Int> = JsonObjectDelegate(this.obj, { it.int }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byLong       ( key: String? = null, default: ( () -> Long       )? = null ) : JsonObjectDelegate<Long> = JsonObjectDelegate(this.obj, { it.long }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byFloat      ( key: String? = null, default: ( () -> Float      )? = null ) : JsonObjectDelegate<Float> = JsonObjectDelegate(this.obj, { it.float }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byDouble     ( key: String? = null, default: ( () -> Double     )? = null ) : JsonObjectDelegate<Double> = JsonObjectDelegate(this.obj, { it.double }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byNumber     ( key: String? = null, default: ( () -> Number     )? = null ) : JsonObjectDelegate<Number> = JsonObjectDelegate(this.obj, { it.number }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byBigInteger ( key: String? = null, default: ( () -> BigInteger )? = null ) : JsonObjectDelegate<BigInteger> = JsonObjectDelegate(this.obj, { it.bigInteger }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byBigDecimal ( key: String? = null, default: ( () -> BigDecimal )? = null ) : JsonObjectDelegate<BigDecimal> = JsonObjectDelegate(this.obj, { it.bigDecimal }, { it.toJsonPrimitive() }, key, default)
fun JsonElement.byArray      ( key: String? = null, default: ( () -> JsonArray  )? = null ) : JsonObjectDelegate<JsonArray> = JsonObjectDelegate(this.obj, { it.array }, { it }, key, default)
fun JsonElement.byObject     ( key: String? = null, default: ( () -> JsonObject )? = null ) : JsonObjectDelegate<JsonObject> = JsonObjectDelegate(this.obj, { it.obj }, { it }, key, default)

val JsonElement.byNullableString     : NullableJsonObjectDelegate<String?> get() = NullableJsonObjectDelegate(this.obj, { it.string }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableBool       : NullableJsonObjectDelegate<Boolean?> get() = NullableJsonObjectDelegate(this.obj, { it.bool }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableByte       : NullableJsonObjectDelegate<Byte?> get() = NullableJsonObjectDelegate(this.obj, { it.byte }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableChar       : NullableJsonObjectDelegate<Char?> get() = NullableJsonObjectDelegate(this.obj, { it.char }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableShort      : NullableJsonObjectDelegate<Short?> get() = NullableJsonObjectDelegate(this.obj, { it.short }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableInt        : NullableJsonObjectDelegate<Int?> get() = NullableJsonObjectDelegate(this.obj, { it.int }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableLong       : NullableJsonObjectDelegate<Long?> get() = NullableJsonObjectDelegate(this.obj, { it.long }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableFloat      : NullableJsonObjectDelegate<Float?> get() = NullableJsonObjectDelegate(this.obj, { it.float }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableDouble     : NullableJsonObjectDelegate<Double?> get() = NullableJsonObjectDelegate(this.obj, { it.double }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableNumber     : NullableJsonObjectDelegate<Number?> get() = NullableJsonObjectDelegate(this.obj, { it.number }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableBigInteger : NullableJsonObjectDelegate<BigInteger?> get() = NullableJsonObjectDelegate(this.obj, { it.bigInteger }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableBigDecimal : NullableJsonObjectDelegate<BigDecimal?> get() = NullableJsonObjectDelegate(this.obj, { it.bigDecimal }, { it?.toJsonPrimitive() ?: jsonNull })
val JsonElement.byNullableArray      : NullableJsonObjectDelegate<JsonArray?> get() = NullableJsonObjectDelegate(this.obj, { it.array }, { it ?: jsonNull })
val JsonElement.byNullableObject     : NullableJsonObjectDelegate<JsonObject?> get() = NullableJsonObjectDelegate(this.obj, { it.obj }, { it ?: jsonNull })

fun JsonElement.byNullableString     (key: String? = null, default: ( () -> String     )? = null) : NullableJsonObjectDelegate<String?> = NullableJsonObjectDelegate(this.obj, { it.string }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableBool       (key: String? = null, default: ( () -> Boolean    )? = null) : NullableJsonObjectDelegate<Boolean?> = NullableJsonObjectDelegate(this.obj, { it.bool }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableByte       (key: String? = null, default: ( () -> Byte       )? = null) : NullableJsonObjectDelegate<Byte?> = NullableJsonObjectDelegate(this.obj, { it.byte }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableChar       (key: String? = null, default: ( () -> Char       )? = null) : NullableJsonObjectDelegate<Char?> = NullableJsonObjectDelegate(this.obj, { it.char }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableShort      (key: String? = null, default: ( () -> Short      )? = null) : NullableJsonObjectDelegate<Short?> = NullableJsonObjectDelegate(this.obj, { it.short }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableInt        (key: String? = null, default: ( () -> Int        )? = null) : NullableJsonObjectDelegate<Int?> = NullableJsonObjectDelegate(this.obj, { it.int }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableLong       (key: String? = null, default: ( () -> Long       )? = null) : NullableJsonObjectDelegate<Long?> = NullableJsonObjectDelegate(this.obj, { it.long }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableFloat      (key: String? = null, default: ( () -> Float      )? = null) : NullableJsonObjectDelegate<Float?> = NullableJsonObjectDelegate(this.obj, { it.float }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableDouble     (key: String? = null, default: ( () -> Double     )? = null) : NullableJsonObjectDelegate<Double?> = NullableJsonObjectDelegate(this.obj, { it.double }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableNumber     (key: String? = null, default: ( () -> Number     )? = null) : NullableJsonObjectDelegate<Number?> = NullableJsonObjectDelegate(this.obj, { it.number }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableBigInteger (key: String? = null, default: ( () -> BigInteger )? = null) : NullableJsonObjectDelegate<BigInteger?> = NullableJsonObjectDelegate(this.obj, { it.bigInteger }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableBigDecimal (key: String? = null, default: ( () -> BigDecimal )? = null) : NullableJsonObjectDelegate<BigDecimal?> = NullableJsonObjectDelegate(this.obj, { it.bigDecimal }, { it?.toJsonPrimitive() ?: jsonNull }, key, default)
fun JsonElement.byNullableArray      (key: String? = null, default: ( () -> JsonArray  )? = null) : NullableJsonObjectDelegate<JsonArray?> = NullableJsonObjectDelegate(this.obj, { it.array }, { it ?: jsonNull }, key, default)
fun JsonElement.byNullableObject     (key: String? = null, default: ( () -> JsonObject )? = null) : NullableJsonObjectDelegate<JsonObject?> = NullableJsonObjectDelegate(this.obj, { it.obj }, { it ?: jsonNull }, key, default)

fun JsonElement.byString     (index: Int) : JsonArrayDelegate<String> = JsonArrayDelegate(this.array, index, { it.string }, { it.toJsonPrimitive() })
fun JsonElement.byBool       (index: Int) : JsonArrayDelegate<Boolean> = JsonArrayDelegate(this.array, index, { it.bool }, { it.toJsonPrimitive() })
fun JsonElement.byByte       (index: Int) : JsonArrayDelegate<Byte> = JsonArrayDelegate(this.array, index, { it.byte }, { it.toJsonPrimitive() })
fun JsonElement.byChar       (index: Int) : JsonArrayDelegate<Char> = JsonArrayDelegate(this.array, index, { it.char }, { it.toJsonPrimitive() })
fun JsonElement.byShort      (index: Int) : JsonArrayDelegate<Short> = JsonArrayDelegate(this.array, index, { it.short }, { it.toJsonPrimitive() })
fun JsonElement.byInt        (index: Int) : JsonArrayDelegate<Int> = JsonArrayDelegate(this.array, index, { it.int }, { it.toJsonPrimitive() })
fun JsonElement.byLong       (index: Int) : JsonArrayDelegate<Long> = JsonArrayDelegate(this.array, index, { it.long }, { it.toJsonPrimitive() })
fun JsonElement.byFloat      (index: Int) : JsonArrayDelegate<Float> = JsonArrayDelegate(this.array, index, { it.float }, { it.toJsonPrimitive() })
fun JsonElement.byDouble     (index: Int) : JsonArrayDelegate<Double> = JsonArrayDelegate(this.array, index, { it.double }, { it.toJsonPrimitive() })
fun JsonElement.byNumber     (index: Int) : JsonArrayDelegate<Number> = JsonArrayDelegate(this.array, index, { it.number }, { it.toJsonPrimitive() })
fun JsonElement.byBigInteger (index: Int) : JsonArrayDelegate<BigInteger> = JsonArrayDelegate(this.array, index, { it.bigInteger }, { it.toJsonPrimitive() })
fun JsonElement.byBigDecimal (index: Int) : JsonArrayDelegate<BigDecimal> = JsonArrayDelegate(this.array, index, { it.bigDecimal }, { it.toJsonPrimitive() })
fun JsonElement.byArray      (index: Int) : JsonArrayDelegate<JsonArray> = JsonArrayDelegate(this.array, index, { it.array }, { it })
fun JsonElement.byObject     (index: Int) : JsonArrayDelegate<JsonObject> = JsonArrayDelegate(this.array, index, { it.obj }, { it })
