sig Student {}
sig Professor {}
sig Class {
	assistant_for: set Student,
	instructor_of: one Professor
}
sig Assignment {
	associated_with: one Class,
	assigned_to: some Student
}

pred PolicyAllowsGrading(s: Student, a: Assignment) {
s in a.associated_with.assistant_for 
s !in a.assigned_to
}

pred test1 {
some disj Student0: Student {some disj Professor0: Professor {some disj Class0, Class1, Class2: Class {some disj Assignment0, Assignment1, Assignment2: Assignment {
Student = Student0
Professor = Professor0
Class = Class0 + Class1 + Class2
assistant_for = Class0->Student0 + Class1->Student0 + Class2->Student0
instructor_of = Class0->Professor0 + Class1->Professor0 + Class2->Professor0
Assignment = Assignment0 + Assignment1 + Assignment2
associated_with = Assignment0->Class2 + Assignment1->Class1 + Assignment2->Class2
assigned_to = Assignment0->Student0 + Assignment1->Student0 + Assignment2->Student0
!PolicyAllowsGrading[Student0,Assignment2]
}}}}
}
run test1 
