package hardfloat
import Chisel._
// import chisel3._
import chisel3.util._

class Float_Inverter extends Module { // 4个周期
  val io = IO(new Bundle {
    val in1 = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val inverter = Module(new fpInverter(23))

  inverter.io.in1 := io.in1(22, 0)

  val in2ExpReg0 = RegInit(0.U(8.W)) // stage 0
  in2ExpReg0 := io.in1(30, 23)
  val in2SignReg0 = RegInit(0.U(1.W)) // stage 0
  in2SignReg0 := io.in1(31)
  val in2ExpReg1 = RegInit(0.U(8.W)) // stage 1
  in2ExpReg1 := in2ExpReg0
  val in2SignReg1 = RegInit(0.U(1.W)) // stage 1
  in2SignReg1 := in2SignReg0

  val in2ExpReg2 = RegInit(0.U(8.W)) // stage 2
  in2ExpReg2 := in2ExpReg1
  val in2SignReg2 = RegInit(0.U(1.W)) // stage 2
  in2SignReg2 := in2SignReg1

  val in2ExpReg3 = RegInit(0.U(8.W)) // stage 3
  in2ExpReg3 := in2ExpReg2
  val in2SignReg3 = RegInit(0.U(1.W)) // stage 3
  in2SignReg3 := in2SignReg2

  val invMantReg = RegInit(0.U(24.W))
  invMantReg := inverter.io.out(23, 0)

  val invMant = invMantReg
  val negExpTmp = 254.U - in2ExpReg3
  val negExp = Mux(invMant === 0.U, negExpTmp, negExpTmp - 1.U)
  io.out := chisel3.util.Cat(in2SignReg3, negExp, invMant(23, 1))
}
/*
class fp_inverter_Test(c: fp_inverter) extends PeekPokeTester(c) {

  // poke(c.io.in1, 1096155136.U)		// 13.375
  poke(c.io.in1, 1091829760.U) // 9.25
  // 1.445945946      3FB914C1
  // 0.108108108     3DDD67C8
  step(1)
  // poke(c.io.in1, 2139095039.U)
  // poke(c.io.in2, 2139095039.U)
  poke(c.io.in1, 1199570944.U) // 65536
  // poke(c.io.in2, 1106771968.U)	//31
//65536     47800000
  // //2114.064453125         45042108
  // 0.000015259    37800000
  step(1)
  poke(c.io.in1, 1082130431.U) // exp = 1, mantissa = 28 ones  3.9999997615814
  // 0.250000015      3E800000
  // poke(c.io.in2, 1106771968.U)	// 31
  //     //0.129032258 3E042107
  step(1)
  poke(c.io.in1, 1207435265.U) // 126976.0078125
  // poke(c.io.in2, 1082130431.U)//3.9999997615814

  // 31744.002032485  46F80001

  step(1)
  poke(c.io.in1, 3268169302L.U) // -102.153
  // poke(c.io.in2, 1108137738.U)//35.21
  // -2.901249645  4039AE12取反
  step(10)
}

object fpinverter_Test extends App {
  chisel3.iotesters.Driver.execute(args, () => new fp_inverter())(c =>
    new fp_inverter_Test(c)
  )
}
 */
