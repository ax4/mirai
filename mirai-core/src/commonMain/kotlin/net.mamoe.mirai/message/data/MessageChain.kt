/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:JvmMultifileClass
@file:JvmName("MessageUtils")
@file:Suppress("unused", "NOTHING_TO_INLINE")

package net.mamoe.mirai.message.data

import net.mamoe.mirai.utils.MiraiExperimentalAPI
import net.mamoe.mirai.utils.MiraiInternalAPI
import net.mamoe.mirai.utils.SinceMirai
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KProperty

/**
 * 消息链. 空的实现为 [EmptyMessageChain]
 *
 * 要获取更多信息, 请查看 [Message]
 *
 * @see buildMessageChain 构造一个 [MessageChain]
 * @see asMessageChain 将单个 [Message] 转换为 [MessageChain]
 * @see asMessageChain 将 [Iterable] 或 [Sequence] 委托为 [MessageChain]
 *
 * @see forEachContent 遍历内容
 *
 * @see orNull 属性委托扩展
 * @see orElse 属性委托扩展
 * @see getValue 属性委托扩展
 * @see flatten 扁平化
 */
interface MessageChain : Message, Iterable<SingleMessage> {
    /**
     * 元素数量. [EmptyMessageChain] 不参加计数.
     */
    @SinceMirai("0.31.1")
    val size: Int

    /**
     * 获取第一个类型为 [key] 的 [Message] 实例
     *
     * @param key 由各个类型消息的伴生对象持有. 如 [PlainText.Key]
     * @throws NoSuchElementException 当找不到这个类型的 [Message] 时
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("first")
    /* final */ operator fun <M : Message> get(key: Message.Key<M>): M = first(key)

    /**
     * 获取第一个类型为 [key] 的 [Message] 实例, 找不到则返回 `null`
     *
     * @param key 由各个类型消息的伴生对象持有. 如 [PlainText.Key]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("firstOrNull")
            /* final */ fun <M : Message> getOrNull(key: Message.Key<M>): M? = firstOrNull(key)

    /**
     * 遍历每一个有内容的消息, 即 [At], [AtAll], [PlainText], [Image], [Face], [XmlMessage], [QuoteReply].
     * 仅供 `Java` 使用
     */
    @Suppress("FunctionName", "INAPPLICABLE_JVM_NAME")
    @JsName("forEachContent")
    @JvmName("forEachContent")
    @MiraiInternalAPI
    fun `__forEachContent for Java__`(block: (Message) -> Unit) {
        this.forEachContent(block)
    }

    /**
     * 遍历每一个消息, 即 [MessageSource] [At], [AtAll], [PlainText], [Image], [Face], [XmlMessage], [QuoteReply].
     * 仅供 `Java` 使用
     */
    @Suppress("FunctionName", "INAPPLICABLE_JVM_NAME")
    @JsName("forEach")
    @JvmName("forEach")
    @MiraiInternalAPI
    fun `__forEach for Java__`(block: (Message) -> Unit) {
        this.forEach(block)
    }
}

// region accessors

/**
 * 遍历每一个 [消息内容][MessageContent]
 */
@SinceMirai("0.39.0")
@JvmSynthetic
inline fun MessageChain.forEachContent(block: (MessageContent) -> Unit) {
    for (element in this) {
        if (element !is MessageMetadata) {
            check(element is MessageContent) { "internal error: Message must be either MessageMetaData or MessageContent" }
            block(element)
        }
    }
}

/**
 * 如果每一个 [消息内容][MessageContent] 都满足 [block], 返回 `true`
 */
@JvmSynthetic
inline fun MessageChain.allContent(block: (MessageContent) -> Boolean): Boolean {
    this.forEach {
        if (it !is MessageMetadata) {
            check(it is MessageContent) { "internal error: Message must be either MessageMetaData or MessageContent" }
            if (!block(it)) return false
        }
    }
    return true
}

/**
 * 如果每一个 [消息内容][MessageContent] 都不满足 [block], 返回 `true`
 */
@JvmSynthetic
inline fun MessageChain.noneContent(block: (MessageContent) -> Boolean): Boolean {
    this.forEach {
        if (it !is MessageMetadata) {
            check(it is MessageContent) { "internal error: Message must be either MessageMetaData or MessageContent" }
            if (block(it)) return false
        }
    }
    return true
}


/**
 * 获取第一个 [M] 类型的 [Message] 实例
 */
@JvmSynthetic
inline fun <reified M : Message?> MessageChain.firstIsInstanceOrNull(): M? = this.firstOrNull { it is M } as M?

/**
 * 获取第一个 [M] 类型的 [Message] 实例
 * @throws [NoSuchElementException] 如果找不到该类型的实例
 */
@JvmSynthetic
inline fun <reified M : Message> MessageChain.firstIsInstance(): M = this.first { it is M } as M

/**
 * 获取第一个 [M] 类型的 [Message] 实例
 */
@JvmSynthetic
inline fun <reified M : Message> MessageChain.anyIsInstance(): Boolean = this.any { it is M }


/**
 * 获取第一个 [M] 类型的 [Message] 实例
 */
@OptIn(MiraiExperimentalAPI::class)
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
fun <M : Message> MessageChain.firstOrNull(key: Message.Key<M>): M? = firstOrNullImpl(key)

/**
 * 获取第一个 [M] 类型的 [Message] 实例
 * @throws [NoSuchElementException] 如果找不到该类型的实例
 */
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
inline fun <M : Message> MessageChain.first(key: Message.Key<M>): M =
    firstOrNull(key) ?: throw NoSuchElementException("Message type ${key.typeName} not found in chain $this")

/**
 * 获取第一个 [M] 类型的 [Message] 实例
 */
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
inline fun <M : Message> MessageChain.any(key: Message.Key<M>): Boolean = firstOrNull(key) != null

// endregion accessors


// region delegate

/**
 * 提供一个类型的值的委托. 若不存在则会抛出异常 [NoSuchElementException]
 *
 * 用法:
 * ```
 * val message: MessageChain
 *
 * val at: At by message
 * val image: Image by message
 */
@JvmSynthetic
inline operator fun <reified T : Message> MessageChain.getValue(thisRef: Any?, property: KProperty<*>): T =
    this.firstIsInstance()

/**
 * 可空的委托
 * @see orNull
 */
inline class OrNullDelegate<out R : Message?>(private val value: Any?) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): R = value as R
}

/**
 * 提供一个类型的 [Message] 的委托, 若不存在这个类型的 [Message] 则委托会提供 `null`
 *
 * 用法:
 * ```
 * val message: MessageChain
 *
 * val at: At? by message.orNull()
 * ```
 * @see orNull 提供一个不存在则 null 的委托
 * @see orElse 提供一个不存在则使用默认值的委托
 */
@JvmSynthetic
inline fun <reified T : Message> MessageChain.orNull(): OrNullDelegate<T?> =
    OrNullDelegate(this.firstIsInstanceOrNull<T>())

/**
 * 提供一个类型的 [Message] 的委托, 若不存在这个类型的 [Message] 则委托会提供 `null`
 *
 * 用法:
 * ```
 * val message: MessageChain
 *
 * val at: At by message.orElse { /* 返回一个 At */  }
 * val atNullable: At? by message.orElse { /* 返回一个 At? */  }
 * ```
 * @see orNull 提供一个不存在则 null 的委托
 */
@Suppress("RemoveExplicitTypeArguments")
@JvmSynthetic
inline fun <reified T : Message?> MessageChain.orElse(
    lazyDefault: () -> T
): OrNullDelegate<T> =
    OrNullDelegate<T>(this.firstIsInstanceOrNull<T>() ?: lazyDefault())

// endregion delegate


// region converters
/**
 * 得到包含 [this] 的 [MessageChain].
 *
 * 若 [this] 为 [MessageChain] 将直接返回 this,
 * 若 [this] 为 [CombinedMessage] 将 [扁平化][flatten] 后委托为 [MessageChain],
 * 否则将调用 [asMessageChain]
 */
@JvmName("newChain")
@JsName("newChain")
@Suppress("UNCHECKED_CAST")
@OptIn(MiraiInternalAPI::class)
fun Message.asMessageChain(): MessageChain = when (this) {
    is MessageChain -> this
    is CombinedMessage -> (this as Iterable<Message>).asMessageChain()
    else -> SingleMessageChainImpl(this as SingleMessage)
}

/**
 * 直接将 [this] 委托为一个 [MessageChain]
 */
@JvmSynthetic
fun SingleMessage.asMessageChain(): MessageChain = SingleMessageChainImpl(this)

/**
 * 直接将 [this] 委托为一个 [MessageChain]
 */
@JvmSynthetic
fun Collection<SingleMessage>.asMessageChain(): MessageChain =
    MessageChainImplByCollection(this.constrainSingleMessages())

/**
 * 将 [this] [扁平化后][flatten] 委托为一个 [MessageChain]
 */
@JvmName("newChain")
// @JsName("newChain")
fun Collection<Message>.asMessageChain(): MessageChain = MessageChainImplBySequence(this.flatten())

/**
 * 直接将 [this] 委托为一个 [MessageChain]
 */
@JvmSynthetic
fun Iterable<SingleMessage>.asMessageChain(): MessageChain =
    MessageChainImplByCollection(this.constrainSingleMessages())

@JvmSynthetic
fun MessageChain.asMessageChain(): MessageChain = this // 避免套娃

/**
 * 将 [this] [扁平化后][flatten] 委托为一个 [MessageChain]
 */
@JvmName("newChain")
// @JsName("newChain")
fun Iterable<Message>.asMessageChain(): MessageChain = MessageChainImplBySequence(this.flatten())

/**
 * 直接将 [this] 委托为一个 [MessageChain]
 */
@JvmSynthetic
fun Sequence<SingleMessage>.asMessageChain(): MessageChain = MessageChainImplBySequence(this)

/**
 * 将 [this] [扁平化后][flatten] 委托为一个 [MessageChain]
 */
@JvmName("newChain")
// @JsName("newChain")
fun Sequence<Message>.asMessageChain(): MessageChain = MessageChainImplBySequence(this.flatten())

/**
 * 构造一个 [MessageChain]
 * 为提供更好的 Java API.
 */
@Suppress("FunctionName")
@JvmName("newChain")
fun _____newChain______(vararg messages: Message): MessageChain {
    return messages.asIterable().asMessageChain()
}

/**
 * 构造一个 [MessageChain]
 * 为提供更好的 Java API.
 */
@Suppress("FunctionName")
@JvmName("newChain")
fun _____newChain______(messages: String): MessageChain {
    return messages.toMessage().asMessageChain()
}

/**
 * 扁平化消息序列.
 *
 * 原 [this]:
 * ```
 * A <- CombinedMessage(B, C) <- D <- MessageChain(E, F, G)
 * ```
 * 结果 [Sequence]:
 * ```
 * A <- B <- C <- D <- E <- F <- G
 * ```
 */
inline fun Iterable<Message>.flatten(): Sequence<SingleMessage> = asSequence().flatten()

// @JsName("flatten1")
@JvmName("flatten1")// avoid platform declare clash
@JvmSynthetic
inline fun Iterable<SingleMessage>.flatten(): Sequence<SingleMessage> = this.asSequence() // fast path

/**
 * 扁平化消息序列.
 *
 * 原 [this]:
 * ```
 * A <- CombinedMessage(B, C) <- D <- MessageChain(E, F, G)
 * ```
 * 结果 [Sequence]:
 * ```
 * A <- B <- C <- D <- E <- F <- G
 * ```
 */
inline fun Sequence<Message>.flatten(): Sequence<SingleMessage> = flatMap { it.flatten() }

@JsName("flatten1") // avoid platform declare clash
@JvmName("flatten1")
@JvmSynthetic
inline fun Sequence<SingleMessage>.flatten(): Sequence<SingleMessage> = this // fast path

/**
 * 扁平化 [Message]
 *
 * 对于不同类型的接收者（receiver）:
 * - `CombinedMessage(A, B)` 返回 `A <- B`
 * - `MessageChain(E, F, G)` 返回 `E <- F <- G`
 * - 其他: 返回 `sequenceOf(this)`
 */
fun Message.flatten(): Sequence<SingleMessage> {
    @OptIn(MiraiInternalAPI::class)
    return when (this) {
        is MessageChain -> this.asSequence()
        is CombinedMessage -> this.asSequence() // already constrained single.
        else -> sequenceOf(this as SingleMessage)
    }
}

@JvmSynthetic // make Java user happier with less methods
inline fun MessageChain.flatten(): Sequence<SingleMessage> = this.asSequence() // fast path

// endregion converters


/**
 * 不含任何元素的 [MessageChain].
 */
object EmptyMessageChain : MessageChain, Iterator<SingleMessage> {

    override val size: Int get() = 0
    override fun toString(): String = ""
    override fun contentToString(): String = ""
    override fun equals(other: Any?): Boolean = other === this

    override fun iterator(): Iterator<SingleMessage> = this
    override fun hasNext(): Boolean = false
    override fun next(): SingleMessage = throw NoSuchElementException("EmptyMessageChain is empty.")
}