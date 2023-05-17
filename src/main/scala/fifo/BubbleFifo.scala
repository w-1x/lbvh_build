package fifo

import config.Configs._
import chisel3._
import chisel3.util._

class BubbleFifo[T <: Data](gen: T, depth: Int)
    extends Fifo(gen: T, depth: Int) {
  private class Buffer() extends Module {
    val io = IO(new FifoIO(gen))

    val fullReg = RegInit(false.B)
    val dataReg = Reg(gen)

    when(fullReg) {
      when(io.deq.ready) {
        fullReg := false.B
      }
    }.otherwise {
      when(io.enq.valid) {
        fullReg := true.B
        dataReg := io.enq.bits
      }
    }

    io.enq.ready := !fullReg
    io.deq.valid := fullReg
    io.deq.bits := dataReg
  }

  private val buffers = Array.fill(depth) {
    Module(new Buffer())
  }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq <> buffers(i).io.deq
  }

  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}
