package com.mynews.common.core.json.gson.extension

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType


@Suppress("PROTECTED_CALL_FROM_PUBLIC_INLINE")
inline fun <reified T : Any> gsonTypeToken(): Type = object : TypeToken<T>() {}.type

fun ParameterizedType.isWildcard(): Boolean {
    var hasAnyWildCard = false
    var hasBaseWildCard = false
    var hasSpecific = false

    val cls = this.rawType as Class<*>
    cls.typeParameters.forEachIndexed { i, variable ->
        val argument = actualTypeArguments[i]

        if (argument is WildcardType) {
            val hit = variable.bounds.firstOrNull { it in argument.upperBounds }
            if (hit != null) {
                if (hit == Any::class.java)
                    hasAnyWildCard = true
                else
                    hasBaseWildCard = true
            } else
                hasSpecific = true
        } else
            hasSpecific = true

    }

    if (hasAnyWildCard && hasSpecific)
        throw IllegalArgumentException("Either none or all type parameters can be wildcard in $this")

    return hasAnyWildCard || (hasBaseWildCard && !hasSpecific)
}

fun removeTypeWildcards(type: Type): Type {

    if (type is ParameterizedType) {
        val arguments = type.actualTypeArguments
                .map { if (it is WildcardType) it.upperBounds[0] else it }
                .map { removeTypeWildcards(it) }
                .toTypedArray()
        return TypeToken.getParameterized(type.rawType, *arguments).type
    }

    return type
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
inline fun <reified T : Any> typeToken(): Type {
    val type = gsonTypeToken<T>()

    if (type is ParameterizedType && type.isWildcard())
        return type.rawType

    return removeTypeWildcards(type)
}


data class SerializerArg<T>(
        val src: T,
        val type: Type,
        val context: Context
) {
    class Context(val gsonContext: JsonSerializationContext) : JsonSerializationContext by gsonContext {
        inline fun <reified T : Any> typedSerialize(src: T) = gsonContext.serialize(src, typeToken<T>())
    }
}

data class DeserializerArg(
        val json: JsonElement,
        val type: Type,
        val context: Context
) {
    class Context(val gsonContext: JsonDeserializationContext) : JsonDeserializationContext by gsonContext {
        inline fun <reified T : Any> deserialize(json: JsonElement) = gsonContext.deserialize<T>(json, typeToken<T>())
    }
}

fun <T : Any> jsonSerializer(serializer: (arg: SerializerArg<T>) -> JsonElement): JsonSerializer<T>
        = JsonSerializer { src, type, context -> serializer(SerializerArg(src, type, SerializerArg.Context(context))) }

fun <T : Any> jsonDeserializer(deserializer: (arg: DeserializerArg) -> T?): JsonDeserializer<T>
        = JsonDeserializer<T> { json, type, context -> deserializer(DeserializerArg(json, type, DeserializerArg.Context(context))) }

fun <T : Any> instanceCreator(creator: (type: Type) -> T): InstanceCreator<T>
        = InstanceCreator { creator(it) }

interface TypeAdapterBuilder<T : Any, R : T?> {
    fun read(function: JsonReader.() -> R)
    fun write(function: JsonWriter.(value: T) -> Unit)
}

internal class TypeAdapterBuilderImpl<T : Any, R : T?>(
        init: TypeAdapterBuilder<T, R>.() -> Unit
) : TypeAdapterBuilder<T, R> {

    private var _readFunction: (JsonReader.() -> R)? = null
    private var _writeFunction: (JsonWriter.(value: T) -> Unit)? = null

    override fun read(function: JsonReader.() -> R) {
        _readFunction = function
    }

    override fun write(function: JsonWriter.(value: T) -> Unit) {
        _writeFunction = function
    }

    fun build(): TypeAdapter<T> = object : TypeAdapter<T>() {
        override fun read(reader: JsonReader) = _readFunction!!.invoke(reader)
        override fun write(writer: JsonWriter, value: T) = _writeFunction!!.invoke(writer, value)
    }

    init {
        init()
        if (_readFunction == null || _writeFunction == null)
            throw IllegalArgumentException("You must define both a read and a write function")
    }
}

fun <T : Any> typeAdapter(init: TypeAdapterBuilder<T, T>.() -> Unit): TypeAdapter<T> = TypeAdapterBuilderImpl(init).build()
fun <T : Any> nullableTypeAdapter(init: TypeAdapterBuilder<T, T?>.() -> Unit): TypeAdapter<T> = TypeAdapterBuilderImpl<T, T?>(init).build().nullSafe()

inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(typeAdapter: Any): GsonBuilder
        = this.registerTypeAdapter(typeToken<T>(), typeAdapter)

inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(serializer: JsonSerializer<T>): GsonBuilder
        = this.registerTypeAdapter<T>(serializer as Any)

inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(deserializer: JsonDeserializer<T>): GsonBuilder
        = this.registerTypeAdapter<T>(deserializer as Any)

inline fun <reified T : Any> GsonBuilder.registerTypeHierarchyAdapter(typeAdapter: Any): GsonBuilder
        = this.registerTypeHierarchyAdapter(T::class.java, typeAdapter)


interface RegistrationBuilder<T : Any, R : T?> : TypeAdapterBuilder<T, R> {
    fun serialize(serializer: (arg: SerializerArg<T>) -> JsonElement)
    fun deserialize(deserializer: (DeserializerArg) -> T?)
    fun createInstances(creator: (type: Type) -> T)
}


internal class RegistrationBuilderImpl<T : Any>(
        val registeredType: Type,
        init: RegistrationBuilder<T, T>.() -> Unit,
        protected val register: (typeAdapter: Any) -> Unit
) : RegistrationBuilder<T, T> {

    protected enum class _API { SD, RW }

    private var _api: _API? = null

    private var _readFunction: (JsonReader.() -> T)? = null
    private var _writeFunction: (JsonWriter.(value: T) -> Unit)? = null

    private fun _checkApi(api: _API) {
        if (_api != null && _api != api)
            throw IllegalArgumentException("You cannot use serialize/deserialize and read/write for the same type")
        _api = api
    }

    override fun serialize(serializer: (arg: SerializerArg<T>) -> JsonElement) {
        _checkApi(_API.SD)
        register(jsonSerializer(serializer))
    }

    override fun deserialize(deserializer: (DeserializerArg) -> T?) {
        _checkApi(_API.SD)
        register(jsonDeserializer(deserializer))
    }

    override fun createInstances(creator: (type: Type) -> T) = register(instanceCreator(creator))

    private fun _registerTypeAdapter() {
        _checkApi(_API.RW)
        val readFunction = _readFunction
        val writeFunction = _writeFunction
        if (readFunction == null || writeFunction == null)
            return
        register(typeAdapter<T> { read(readFunction); write(writeFunction) })
        _readFunction = null
        _writeFunction = null
    }

    override fun read(function: JsonReader.() -> T) {
        _readFunction = function
        _registerTypeAdapter()
    }

    override fun write(function: JsonWriter.(value: T) -> Unit) {
        _writeFunction = function
        _registerTypeAdapter()
    }

    init {
        init()
        if (_readFunction != null)
            throw IllegalArgumentException("You cannot define a read function without a write function")
        if (_writeFunction != null)
            throw IllegalArgumentException("You cannot define a write function without a read function")
    }
}


fun <T : Any> GsonBuilder.registerTypeAdapterBuilder(type: Type, init: RegistrationBuilder<T, T>.() -> Unit): GsonBuilder {
    RegistrationBuilderImpl(type, init) { registerTypeAdapter(type, it) }
    return this
}

inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(noinline init: RegistrationBuilder<T, T>.() -> Unit): GsonBuilder
        = registerTypeAdapterBuilder(typeToken<T>(), init)


fun <T : Any> GsonBuilder.registerNullableTypeAdapterBuilder(type: Type, init: TypeAdapterBuilder<T, T?>.() -> Unit): GsonBuilder {
    registerTypeAdapter(type, nullableTypeAdapter(init))
    return this
}

inline fun <reified T : Any> GsonBuilder.registerNullableTypeAdapter(noinline init: TypeAdapterBuilder<T, T?>.() -> Unit): GsonBuilder
        = registerNullableTypeAdapterBuilder(typeToken<T>(), init)


fun <T : Any> GsonBuilder.registerTypeHierarchyAdapterBuilder(type: Class<T>, init: RegistrationBuilder<T, T>.() -> Unit): GsonBuilder {
    RegistrationBuilderImpl(type, init) { registerTypeHierarchyAdapter(type, it) }
    return this
}

inline fun <reified T : Any> GsonBuilder.registerTypeHierarchyAdapter(noinline init: RegistrationBuilder<T, T>.() -> Unit): GsonBuilder
        = registerTypeHierarchyAdapterBuilder(T::class.java, init)


fun <T : Any> GsonBuilder.registerNullableTypeHierarchyAdapterBuilder(type: Class<T>, init: TypeAdapterBuilder<T, T?>.() -> Unit): GsonBuilder {
    registerTypeHierarchyAdapter(type, nullableTypeAdapter(init))
    return this
}

inline fun <reified T : Any> GsonBuilder.registerNullableTypeHierarchyAdapter(noinline init: TypeAdapterBuilder<T, T?>.() -> Unit): GsonBuilder
        = registerNullableTypeHierarchyAdapterBuilder(T::class.java, init)
