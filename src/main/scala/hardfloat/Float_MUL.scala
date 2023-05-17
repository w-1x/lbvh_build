package hardfloat
import chisel3._

class Float_MUL extends Module {
  val io = IO(new Bundle {
    val a = Input(Bits(32.W))
    val b = Input(Bits(32.W))

    val actual = new Bundle {
      val out = Output(Bits(32.W))
      val exceptionFlags = Output(Bits(5.W))
    }
    val check = Output(Bool())
    val pass = Output(Bool())
  })

  val a = RegInit(0.U(32.W))
  val b = RegInit(0.U(32.W))
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

  val fmul = Module(new ValExec_MulAddRecF32_mul)
  fmul.io.a := a
  fmul.io.b := b
  fmul.io.roundingMode := roundingMode
  fmul.io.detectTininess := detectTininess
  fmul.io.expected.out := expected_out
  fmul.io.expected.exceptionFlags := expected_exceptionalFlags
  expected_recOut := fmul.io.expected.recOut

  actual_exceptionFlags := fmul.io.actual.exceptionFlags
  actual_out := fmul.io.actual.out
  check := fmul.io.check
  pass := fmul.io.pass

  // io.actual.out := actual_out
  io.actual.exceptionFlags := actual_exceptionFlags
  io.check := check
  io.pass := pass

  io.actual.out := fNFromRecFN(8, 24, fmul.io.actual.out)
}
