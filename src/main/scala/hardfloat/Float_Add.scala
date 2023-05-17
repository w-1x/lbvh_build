package hardfloat
import Chisel._
// import chisel3 ._
import consts._

class Float_Add extends Module {
  val io = IO(new Bundle {
    val a = Input(Bits(32.W))
    val b = Input(Bits(32.W))
    // val c = Input(Bits(32.W))
    val out = Output(Bits(32.W))
  })
  val premul_a = RegInit(0.U(24.W))
  val premul_b = RegInit(0.U(24.W))
  val premul_c = RegInit(0.U(48.W))
  val isSigNaNAny = RegInit(0.U(1.W))
  val isNaNAOrB = RegInit(0.U(1.W))
  val isInfA = RegInit(0.U(1.W))
  val isZeroA = RegInit(0.U(1.W))
  val isInfB = RegInit(0.U(1.W))
  val isZeroB = RegInit(0.U(1.W))
  val signProd = RegInit(0.U(1.W))
  val isNaNC = RegInit(0.U(1.W))
  val isInfC = RegInit(0.U(1.W))
  val isZeroC = RegInit(0.U(1.W))
  val sExpSum = RegInit(0.S(10.W))
  val doSubMags = RegInit(0.U(1.W))
  val CIsDominant = RegInit(0.U(1.W))
  val CDom_CAlignDist = RegInit(0.U(5.W))
  val highAlignedSigC = RegInit(0.U(26.W))
  val bit0AlignedSigC = RegInit(0.U(1.W))

  val mulAddRecFNToRaw_preMul =
    Module(new MulAddRecFNToRaw_preMul(8, 24))

  mulAddRecFNToRaw_preMul.io.op := UInt(0)
  mulAddRecFNToRaw_preMul.io.a := recFNFromFN(8, 24, io.a)
  mulAddRecFNToRaw_preMul.io.b := UInt(BigInt(1) << 31)
  mulAddRecFNToRaw_preMul.io.c := recFNFromFN(8, 24, io.b)
//-------------------------------------------MulAddRecFN_interIo(toPostMul)
  isSigNaNAny := mulAddRecFNToRaw_preMul.io.toPostMul.isSigNaNAny
  isNaNAOrB := mulAddRecFNToRaw_preMul.io.toPostMul.isNaNAOrB
  isInfA := mulAddRecFNToRaw_preMul.io.toPostMul.isInfA
  isZeroA := mulAddRecFNToRaw_preMul.io.toPostMul.isZeroA
  isInfB := mulAddRecFNToRaw_preMul.io.toPostMul.isInfB
  isZeroB := mulAddRecFNToRaw_preMul.io.toPostMul.isZeroB
  signProd := mulAddRecFNToRaw_preMul.io.toPostMul.signProd
  isNaNC := mulAddRecFNToRaw_preMul.io.toPostMul.isNaNC
  isInfC := mulAddRecFNToRaw_preMul.io.toPostMul.isInfC
  isZeroC := mulAddRecFNToRaw_preMul.io.toPostMul.isZeroC
  sExpSum := mulAddRecFNToRaw_preMul.io.toPostMul.sExpSum
  doSubMags := mulAddRecFNToRaw_preMul.io.toPostMul.doSubMags
  CIsDominant := mulAddRecFNToRaw_preMul.io.toPostMul.CIsDominant
  CDom_CAlignDist := mulAddRecFNToRaw_preMul.io.toPostMul.CDom_CAlignDist
  highAlignedSigC := mulAddRecFNToRaw_preMul.io.toPostMul.highAlignedSigC
  bit0AlignedSigC := mulAddRecFNToRaw_preMul.io.toPostMul.bit0AlignedSigC
//-------------------------------------------step(2)

  // val premul_a_1           = RegInit(0.U(24.W))
  // val premul_b_1           = RegInit(0.U(24.W))
  // val premul_c_1           = RegInit(0.U(48.W))
  // val isSigNaNAny_1    = RegInit(0.U(1.W))
  // val isNaNAOrB_1       = RegInit(0.U(1.W))
  // val isInfA_1                   = RegInit(0.U(1.W))
  // val isZeroA_1               = RegInit(0.U(1.W))
  // val isInfB_1                   = RegInit(0.U(1.W))
  // val isZeroB_1               = RegInit(0.U(1.W))
  // val signProd_1            = RegInit(0.U(1.W))
  // val isNaNC_1               = RegInit(0.U(1.W))
  // val isInfC_1                  = RegInit(0.U(1.W))
  // val isZeroC_1              = RegInit(0.U(1.W))
  // val sExpSum_1           = RegInit(0.S(10.W))
  // val doSubMags_1      = RegInit(0.U(1.W))
  // val CIsDominant_1    = RegInit(0.U(1.W))
  // val CDom_CAlignDist_1 = RegInit(0.U(5.W))
  // val highAlignedSigC_1    = RegInit(0.U(26.W))
  // val bit0AlignedSigC_1     = RegInit(0.U(1.W))

  // isSigNaNAny_1                                             := isSigNaNAny
  // isNaNAOrB_1                                                := isNaNAOrB
  // isInfA_1                                                           := isInfA
  // isZeroA_1                                                       := isZeroA
  // isInfB_1                                                           := isInfB
  // isZeroB_1                                                       := isZeroB
  // signProd_1                                                    := signProd
  // isNaNC_1                                                       := isNaNC
  // isInfC_1                                                          := isInfC
  // isZeroC_1                                                      := isZeroC
  // sExpSum_1                                                   := sExpSum
  // doSubMags_1                                              := doSubMags
  // CIsDominant_1                                           := CIsDominant
  // CDom_CAlignDist_1                                  := CDom_CAlignDist
  // highAlignedSigC_1                                    := highAlignedSigC
  // bit0AlignedSigC_1                                     := bit0AlignedSigC

//-------------------------------------------MulAddRecFN_interIo(a,b,c)
  premul_a := mulAddRecFNToRaw_preMul.io.mulAddA
  premul_b := mulAddRecFNToRaw_preMul.io.mulAddB
  premul_c := mulAddRecFNToRaw_preMul.io.mulAddC

  // premul_a_1                                             := premul_a
  // premul_b_1                                             := premul_b
  // premul_c_1                                              := premul_c

//-------------------------------------------MulAddRecFNToRaw_postMul
  val mulAddResult =
    (premul_a * premul_b) +& premul_c // +&是一个加，但是位数多一位，可以判断溢出

  val mulAddRecFNToRaw_postMul =
    Module(new MulAddRecFNToRaw_postMul(8, 24))
  mulAddRecFNToRaw_postMul.io.roundingMode := UInt(0)
  mulAddRecFNToRaw_postMul.io.mulAddResult := mulAddResult
  mulAddRecFNToRaw_postMul.io.fromPreMul.isSigNaNAny := isSigNaNAny
  mulAddRecFNToRaw_postMul.io.fromPreMul.isNaNAOrB := isNaNAOrB
  mulAddRecFNToRaw_postMul.io.fromPreMul.isInfA := isInfA
  mulAddRecFNToRaw_postMul.io.fromPreMul.isZeroA := isZeroA
  mulAddRecFNToRaw_postMul.io.fromPreMul.isInfB := isInfB
  mulAddRecFNToRaw_postMul.io.fromPreMul.isZeroB := isZeroB
  mulAddRecFNToRaw_postMul.io.fromPreMul.signProd := signProd
  mulAddRecFNToRaw_postMul.io.fromPreMul.isNaNC := isNaNC
  mulAddRecFNToRaw_postMul.io.fromPreMul.isInfC := isInfC
  mulAddRecFNToRaw_postMul.io.fromPreMul.isZeroC := isZeroC
  mulAddRecFNToRaw_postMul.io.fromPreMul.sExpSum := sExpSum
  mulAddRecFNToRaw_postMul.io.fromPreMul.doSubMags := doSubMags
  mulAddRecFNToRaw_postMul.io.fromPreMul.CIsDominant := CIsDominant
  mulAddRecFNToRaw_postMul.io.fromPreMul.CDom_CAlignDist := CDom_CAlignDist
  mulAddRecFNToRaw_postMul.io.fromPreMul.highAlignedSigC := highAlignedSigC
  mulAddRecFNToRaw_postMul.io.fromPreMul.bit0AlignedSigC := bit0AlignedSigC

  val roundRawFNToRecFN =
    Module(new RoundRawFNToRecFN(8, 24, 0))
  roundRawFNToRecFN.io.invalidExc := mulAddRecFNToRaw_postMul.io.invalidExc
  roundRawFNToRecFN.io.infiniteExc := Bool(false)
  roundRawFNToRecFN.io.in := mulAddRecFNToRaw_postMul.io.rawOut
  roundRawFNToRecFN.io.roundingMode := UInt(0)
  roundRawFNToRecFN.io.detectTininess := UInt(1)
  io.out := fNFromRecFN(8, 24, roundRawFNToRecFN.io.out)
}
