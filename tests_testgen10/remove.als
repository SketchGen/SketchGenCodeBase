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

pred Test1{
  some disj List0 : List | some disj Node0, Node1, Node2 : Node | some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    header = List0->Node0
    h_post = List0->Node0
    elem = Node0->Object0 + Node1->Object1 + Node2->Object2
    e_post = Node0->Object0 + Node1->Object1 + Node2->Object2
    link = Node0->Node1 + Node1->Node2
    l_post = Node0->Node1
    Remove[List0,Object2]
  }
}

pred Test2{
  some disj List0 : List | some disj Node0, Node1 : Node | some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1 + Object2
    header = List0->Node0
    h_post = List0->Node1
    elem = Node0->Object0 + Node1->Object1
    e_post = Node0->Object0 + Node1->Object1
    link = Node0->Node1
    no l_post
    !Remove[List0,Object1]
  }
}

pred Test3{
  some disj List0 : List | some disj Node0 : Node | some disj Object0 : Object {
    List = List0
    Node = Node0
    Object = Object0
    no header
    no h_post
    elem = Node0->Object0
    e_post = Node0->Object0
    link = Node0->Node0
    l_post = Node0->Node0
    Remove[List0,Object0]
  }
}

pred Test4{
  some disj List0 : List | some disj Node0 : Node | some disj Object0 : Object {
    List = List0
    Node = Node0
    Object = Object0
    header=List0->Node0
    h_post= List0 -> Node0
    elem = Node0->Object0
    e_post = Node0->Object0
    link = Node0->Node0
    no l_post
    !Remove[List0,Object0]
  }
}


pred Test5{
  some disj List0 : List | some disj Node0, Node1 : Node | some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    header=List0->Node0
    h_post= List0 -> Node0
    elem = Node0->Object0 + Node1->Object1
    e_post = Node0->Object0 + Node1 -> Object1
    link = Node0->Node0  + Node1->Node0
    l_post = Node0 -> Node0 + Node1->Node0
    Remove[List0,Object1]
  }
}

pred Test6{
  some disj List0 : List | some disj Node0, Node1 : Node | some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    header=List0->Node0
    no h_post
    elem = Node0->Object0 + Node1->Object1
    e_post = Node0->Object0 + Node1->Object1
    link = Node0->Node0 + Node1->Node0
    l_post = Node0 -> Node0
    Remove[List0,Object0]
  }
}

pred Test7{
  some disj List0 : List | some disj Object0 : Object {
    List = List0
    no Node
    Object = Object0
    no header
    no h_post
    no elem
    no e_post
    no link
    no l_post
    Remove[List0,Object0]
  }
}

pred Test8{
  some disj List0 : List | some disj Node0, Node1 : Node | some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    header = List0->Node1
    h_post = List0->Node0
    elem = Node0->Object0 + Node1->Object1
    e_post = Node0->Object0 + Node1->Object1
    link = Node0->Node1 + Node1->Node1
    l_post = Node0->Node0
    !Remove[List0,Object0]
  }
}

pred Test9{
  some disj List0 : List | some disj Node0, Node1 : Node | some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    header = List0->Node1
    h_post = List0->Node1
    elem = Node0->Object0 + Node1->Object1
    e_post = Node0->Object0 + Node1->Object1
    link = Node1->Node0 + Node0->Node0
    l_post = Node0->Node1 + Node1->Node1
    Remove[List0,Object0]
  }
}
