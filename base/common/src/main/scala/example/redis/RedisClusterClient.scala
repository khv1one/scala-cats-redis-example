package example.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

import scala.jdk.CollectionConverters.SeqHasAsJava

trait RedisClient extends RedisOperationsIO  {
  protected val client: RedissonClient
}

class RedisClusterClient(hosts: Seq[String]) extends RedisClient {

  private val config: Config = {
    val config = new Config()

    config
      .useClusterServers()
      .setNodeAddresses(hosts.map(host => s"redis://$host").asJava)

    config
  }

  protected val client: RedissonClient = Redisson.create(config)
}

object RedisClusterClient {
  def apply(hosts: Seq[String]): RedisClusterClient = new RedisClusterClient(hosts)
}