module grandpa

abstract sig Person {
    father: lone Man,
    mother: lone Woman
    }

sig Man extends Person { wife: lone Woman }

sig Woman extends Person { husband: lone Man }

fact Biology { no p: Person | p in p.^(mother+father) }

fact Terminology { wife = ~husband }

fact SocialConvention {
    // Generating wife & *(mother+father).mother requires cost >= 8
    no \E\
    no husband & *(mother+father).father
    }

fun grandpas [p: Person]: set Person {
    let parent = mother + father + father.wife + mother.husband |
        p.parent.parent & Man
    }

pred ownGrandpa [m: Man] { m in grandpas[m]  }

run ownGrandpa for 4 Person expect 1
