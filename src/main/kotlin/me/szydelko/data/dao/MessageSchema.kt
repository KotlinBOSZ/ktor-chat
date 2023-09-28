package me.szydelko.data.dao

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import me.szydelko.data.dto.MessageDto
import me.szydelko.service.MessageDataSource
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


class MessageService(private val database: Database) : MessageDataSource {

    object Messages : IntIdTable() {
        val text = text("text")
        val username = varchar("name", length = 50)
        val timestamp = long("timestamp")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Messages)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAllMessages(): List<MessageDto> = dbQuery {
        Messages
            .selectAll().orderBy(Messages.timestamp to SortOrder.DESC)
            .map { MessageDto(it[Messages.text], it[Messages.username], it[Messages.timestamp]) }
    }

    override suspend fun insertMessage(messageDto: MessageDto) {
        dbQuery {
           Messages.insert {
               it[text] = messageDto.text
               it[username] = messageDto.username
               it[timestamp] = messageDto.timestamp
           }
        }
    }


}