package reactjs

case class TestClass(firstName: String, lastName: String) {
  def withFirstName(v: String): TestClass = copy(firstName = v)
  def withLastName(v: String): TestClass = copy(lastName = v)
}
