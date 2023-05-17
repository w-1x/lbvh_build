circuit ModuleSample :
  module ModuleSample :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip in_a : SInt<4>, out_b : SInt<4>}

    io.out_b <= io.in_a @[ModuleSample.scala 9:12]

