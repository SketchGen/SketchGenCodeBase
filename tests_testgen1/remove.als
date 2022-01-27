one sig List {
  header, h_post: lone Node
}

sig Node {
  elem, e_post: lone Object,
  link, l_post: lone Node
}

sig Object {}

pred Remove(l: List, e: Object) {
l.header.*link.elem - e = l.h_post.*l_post.e_post
}

pred Test0{
  some disj List0 : List | some disj Node0, Node1, Node2 : Node | some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    header = List0->Node0
    h_post = List0->Node0
    elem = Node0->Object0 + Node1->Object1 + Node2->Object2
    e_post = Node0->Object0 + Node1->Object1 + Node2->Object2
    link = Node0->Node1 + Node1->Node2
    l_post = Node0->Node1 + Node1->Node2
    !Remove[List0,Object2]
  }
}
