module unknown
open util/integer [] as integer
sig Student {}
sig Professor {}
sig Class {
assistant_for: (set Student),
instructor_of: (one Professor)
}
sig Assignment {
associated_with: (one Class),
assigned_to: (some Student)
}