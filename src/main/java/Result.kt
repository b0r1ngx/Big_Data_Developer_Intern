import org.apache.log4j.BasicConfigurator
import org.pcap4j.core.*
import org.pcap4j.core.BpfProgram.BpfCompileMode
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.packet.Packet
import org.pcap4j.util.NifSelector
import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    //set filter if need
    var pkgSize = 0
    var filter: String? = null
    if (args.isNotEmpty()) filter = args[0]
    val nif = NifSelector().selectNetworkInterface() ?: exitProcess(1)
    val snapLen = 65536
    val mode = PromiscuousMode.PROMISCUOUS
    val timeout = 10 //in millis
    val handle = nif.openLive(snapLen, mode, timeout)
    if (filter != null && filter.isNotEmpty()) {
        handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE)
    }
    val listener = PacketListener { packet ->
        printPacket(packet, handle)
        pkgSize += packet.length() // instead of rawData.size, for maybe minimaze time, when take info from every packet
    }
    handle.loop(50, listener)
    handle.close()
    if (pkgSize < 1024 || pkgSize > 1073741824) println("send info to kafka, topic 'alerts'...")
    println(pkgSize)
}

private fun printPacket(packet: Packet, ph: PcapHandle) {
    val sb = StringBuilder()
    sb.append("A packet captured at ")
        .append(ph.timestamp)
        .append(":")
        .append("\n")
        .append(packet.length())
    println(sb)
    println(packet)
}