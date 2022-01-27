one sig List {
  header, h_post: lone Node
}

sig Node {
  elem, e_post: lone Object,
  link, l_post: lone Node
}

sig Object {}

pred Remove(l: List, e: Object) {
   l.header.*link.elem - e = \E\
}
