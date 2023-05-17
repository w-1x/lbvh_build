package hardfloat
import chisel3._

class Float_MULADD extends Module {
  val io = IO(new Bundle {
    val a = Input(Bits(32.W))
    val b = Input(Bits(32.W))
    val c = Input(Bits(32.W))
    val roundingMode = Input(UInt(3.W))
    val detectTininess = Input(UInt(1.W))

    val expected = new Bundle {
      val out = Input(Bits(32.W))
      val exceptionFlags = Input(Bits(5.W))
      val recOut = Output(Bits(33.W))
    }

    val actual = new Bundle {
      val out = Output(Bits(32.W))
      val exceptionFlags = Output(Bits(5.W))
    }

    val check = Output(Bool())
    val pass = Output(Bool())
  })

  val a = RegInit(0.U(32.W))
  val b = RegInit(0.U(32.W))
  val c = RegInit(0.U(32.W))
  val roundingMode = RegInit(0.U(3.W))
  val detectTininess = RegInit(0.U(1.W))
  val expected_out = RegInit(0.U(32.W))
  val expected_exceptionalFlags = RegInit(0.U(5.W))
  val expected_recOut = RegInit(0.U(33.W))
  val actual_out = RegInit(0.U(32.W))
  val actual_exceptionFlags = RegInit(0.U(5.W))
  val check = RegInit(0.U(1.W))
  val pass = RegInit(0.U(1.W))

  a := io.a
  b := io.b
  c := io.c
  roundingMode := io.roundingMode
  detectTininess := io.detectTininess
  expected_out := io.expected.out
  expected_exceptionalFlags := io.expected.exceptionFlags
  val muladd = Module(new ValExec_MulAddRecF32)

  muladd.io.a := a
  muladd.io.b := b
  muladd.io.c := c
  muladd.io.roundingMode := roundingMode
  muladd.io.detectTininess := detectTininess

  muladd.io.expected.out := expected_out
  muladd.io.expected.exceptionFlags := expected_exceptionalFlags
  expected_recOut := muladd.io.expected.recOut

  actual_out := muladd.io.actual.out
  actual_exceptionFlags := muladd.io.actual.exceptionFlags

  check := muladd.io.check
  pass := muladd.io.pass

  io.expected.recOut := expected_recOut
  // io.actual.out := actual_out
  io.actual.exceptionFlags := actual_exceptionFlags
  io.check := check
  io.pass := pass

  io.actual.out := fNFromRecFN(8, 24, muladd.io.actual.out)
}

// class TesterSimple (dut:MULAdd) extends PeekPokeTester(dut){
//     poke(dut.io.a,1067450368.U)
//     poke(dut.io.b,1067450368.U)
//     poke(dut.io.c,1067030937.U)
//     poke(dut.io.detectTininess,0.U)
//     poke(dut.io.roundingMode,0.U)
//     step(1)
//     println("Result is :" +peek(dut.io.actual.out).toString)
//     }
//     object TesterSimple extends App{
//     chisel3. iotesters.Driver(()=> new MULAdd()){ c =>
//         new TesterSimple(c)
//     }
// }
// object MUL extends App {
//   (new chisel3.stage.ChiselStage).emitVerilog(new FMUL())
// }
