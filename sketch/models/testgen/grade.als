sig Student  {}
sig Professor  {}
sig Class {
	assistant_for: set Student,
	instructor_of: one Professor
}
sig Assignment {
	associated_with: one Class,
	assigned_to: some Student
}

pred PolicyAllowsGrading(s: Student, a: Assignment) {
  // Generating a.associated_with.assistant_for requires cost >= 5
	s in \E\ 
	s !in a.assigned_to
}
