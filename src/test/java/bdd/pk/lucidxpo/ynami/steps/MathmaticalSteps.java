package bdd.pk.lucidxpo.ynami.steps;

import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import bdd.pk.lucidxpo.ynami.scenarioworld.MathScenarioWorld;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathmaticalSteps extends AbstractSteps {
    @Autowired
    private MathScenarioWorld mathScenarioWorld;

    @After
    public void tearDown() {
        releaseFluent();
    }

    @Given("I am about to perform an operation")
    public void iAmAboutToPerformAnOperation() {
    }

    @When("^I enter \"([^\"]*)\" as first operand$")
    public void iEnterAsFirstOperand(final String firstOperand) {
        mathScenarioWorld.setFirstOperand(parseInt(firstOperand));
    }

    @And("^I enter \"([^\"]*)\" as second operand$")
    public void iEnterAsSecondOperand(final String secondOperand) {
        mathScenarioWorld.setSecondOperand(parseInt(secondOperand));
    }

    @Then("^The result of addition operation is \"([^\"]*)\"$")
    public void theResultOfAdditionOperationIs(final String result) {
        assertEquals(parseInt(result), mathScenarioWorld.getFirstOperand() + mathScenarioWorld.getSecondOperand());
    }

    @Then("^The result of subtraction operation is \"([^\"]*)\"$")
    public void theResultOfSubtractionOperationIs(final String result) {
        assertEquals(parseInt(result), mathScenarioWorld.getFirstOperand() - mathScenarioWorld.getSecondOperand());
    }

    @Then("^The result of multiplication operation is \"([^\"]*)\"$")
    public void theResultOfMultiplicationOperationIs(final String result) {
        assertEquals(parseInt(result), mathScenarioWorld.getFirstOperand() * mathScenarioWorld.getSecondOperand());
    }
}
