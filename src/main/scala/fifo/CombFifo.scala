package fifo

import config.Configs._
import chisel3._
import chisel3.util._

class CombFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {
  val memFifo = Module(new MemFifo(gen, depth))
  val bufferFifo = Module(new DoubleBufferFifo(gen, 2))
  io.enq <> memFifo.io.enq
  memFifo.io.deq <> bufferFifo.io.enq
  bufferFifo.io.deq <> io.deq
}
