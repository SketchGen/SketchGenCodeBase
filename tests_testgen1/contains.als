one sig List {
  header: lone Node
}

sig Node {
  elem: lone Object,
  link: lone Node
}

sig Object {}

pred Contains(l: List, e: Object) {
   e in l.header.*link.elem
}


pred Test0{
  some disj List0 : List |  some disj Node0 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0
    no header
    no link
    !Contains[List0, Object2]
  }
}

run Test0 for 3
