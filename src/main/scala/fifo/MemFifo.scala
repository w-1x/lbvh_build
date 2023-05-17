package fifo

import config.Configs._
import chisel3._
import chisel3.util._

class MemFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {
  def counter(depth: Int, incr: Bool): (UInt, UInt) = {
    val cntReg = RegInit(0.U(log2Ceil(depth).W))
    val nextVal = Mux(cntReg === (depth - 1).U, 0.U, cntReg + 1.U)
    when(incr) {
      cntReg := nextVal
    }
    (cntReg, nextVal)
  }

  val mem = SyncReadMem(depth, gen)

  val incrRead = WireDefault(false.B)
  val incrWrite = WireDefault(false.B)
  val (readPtr, nextRead) = counter(depth, incrRead)
  val (writePtr, nextWrite) = counter(depth, incrWrite)

  val emptyReg = RegInit(true.B)
  val fullReg = RegInit(false.B)

  val idle :: valid :: full :: Nil = Enum(3)
  val stateReg = RegInit(idle)
  val shadowReg = Reg(gen)

  // 写FIFO的处理是一样的
  when(io.enq.valid && !fullReg) {
    mem.write(writePtr, io.enq.bits)
    emptyReg := false.B
    fullReg := nextWrite === readPtr
    incrWrite := true.B
  }

  // 读基于内存的FIFO时要处理一个时钟周期的延迟
  val data = mem.read(readPtr)

  switch(stateReg) {
    is(idle) {
      when(!emptyReg) {
        stateReg := valid
        fullReg := false.B
        emptyReg := nextRead === writePtr
        incrRead := true.B
      }
    }
    is(valid) {
      when(io.deq.ready) {
        when(!emptyReg) {
          stateReg := valid
          fullReg := false.B
          emptyReg := nextRead === writePtr
          incrRead := true.B
        }.otherwise {
          stateReg := idle
        }
      }.otherwise {
        shadowReg := data
        stateReg := full
      }
    }
    is(full) {
      when(io.deq.ready) {
        when(!emptyReg) {
          stateReg := valid
          fullReg := false.B
          emptyReg := nextRead === writePtr
          incrRead := true.B
        }.otherwise {
          stateReg := idle
        }
      }
    }
  }

  io.deq.bits := Mux(stateReg === valid, data, shadowReg)
  io.enq.ready := !fullReg
  io.deq.valid := stateReg === valid || stateReg === full
}
