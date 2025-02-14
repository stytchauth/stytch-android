package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class GenerateProductComponentsOrderingTest(
    private val testCase: ProductComponentOrderingTestCase,
) {
    @Test
    fun `Generates expected outputs`() {
        val actual =
            testCase.products.generateProductComponentsOrdering(
                authFlowType = testCase.authFlowType,
                organization = testCase.organization,
            )
        println("EXPECTED: ${testCase.expected}")
        println("ACTUAL: $actual")
        assert(actual == testCase.expected)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testCases(): Collection<ProductComponentOrderingTestCase> =
            listOf(
                TEST_NO_INPUTS,
                TEST_NO_SSO_DISCOVERY,
                TEST_NO_SSO_WITH_NO_CONNECTIONS,
                TEST_SSO_WITH_VALID_CONNECTIONS,
                TEST_INPUTS_FIRST,
                TEST_INPUTS_MIDDLE,
                TEST_INPUTS_MIDDLE_MULTIPLE_INPUTS_ONE_COMPONENT,
                TEST_INPUTS_LAST,
                TEST_MULTI_INPUTS_ONE_COMPONENT,
                TEST_EML_DISCOVERY,
                TEST_EOTP_DISCOVERY,
                TEST_EMAIL_PASSWORD_COMBINED,
                TEST_PASSWORD_ONLY,
            )
    }
}

internal data class ProductComponentOrderingTestCase(
    val name: String,
    val products: List<StytchB2BProduct>,
    val authFlowType: AuthFlowType,
    val organization: InternalOrganizationData?,
    val expected: List<ProductComponent>,
) {
    override fun toString(): String = name
}

internal val TEST_NO_INPUTS =
    ProductComponentOrderingTestCase(
        name = "No Inputs",
        products = listOf(StytchB2BProduct.OAUTH),
        authFlowType = AuthFlowType.DISCOVERY,
        organization = null,
        expected = listOf(ProductComponent.OAuthButtons),
    )

internal val TEST_NO_SSO_DISCOVERY =
    ProductComponentOrderingTestCase(
        name = "No SSO in Discovery",
        products = listOf(StytchB2BProduct.OAUTH, StytchB2BProduct.SSO),
        authFlowType = AuthFlowType.DISCOVERY,
        organization = null,
        expected = listOf(ProductComponent.OAuthButtons),
    )

internal val TEST_NO_SSO_WITH_NO_CONNECTIONS =
    ProductComponentOrderingTestCase(
        name = "No SSO with no connections",
        products = listOf(StytchB2BProduct.OAUTH, StytchB2BProduct.SSO),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns emptyList()
            },
        expected = listOf(ProductComponent.OAuthButtons),
    )

internal val TEST_SSO_WITH_VALID_CONNECTIONS =
    ProductComponentOrderingTestCase(
        name = "SSO with valid connections",
        products = listOf(StytchB2BProduct.OAUTH, StytchB2BProduct.SSO),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns listOf(mockk(relaxed = true))
            },
        expected = listOf(ProductComponent.OAuthButtons, ProductComponent.SSOButtons),
    )

internal val TEST_INPUTS_FIRST =
    ProductComponentOrderingTestCase(
        name = "Inputs first",
        products = listOf(StytchB2BProduct.EMAIL_MAGIC_LINKS, StytchB2BProduct.OAUTH, StytchB2BProduct.SSO),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns listOf(mockk(relaxed = true))
            },
        expected =
            listOf(
                ProductComponent.EmailForm,
                ProductComponent.Divider,
                ProductComponent.OAuthButtons,
                ProductComponent.SSOButtons,
            ),
    )

internal val TEST_INPUTS_MIDDLE =
    ProductComponentOrderingTestCase(
        name = "Inputs in middle",
        products =
            listOf(
                StytchB2BProduct.OAUTH,
                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                StytchB2BProduct.SSO,
            ),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns listOf(mockk(relaxed = true))
            },
        expected =
            listOf(
                ProductComponent.OAuthButtons,
                ProductComponent.Divider,
                ProductComponent.EmailForm,
                ProductComponent.Divider,
                ProductComponent.SSOButtons,
            ),
    )
internal val TEST_INPUTS_MIDDLE_MULTIPLE_INPUTS_ONE_COMPONENT =
    ProductComponentOrderingTestCase(
        name = "Inputs in middle with multiple input components",
        products =
            listOf(
                StytchB2BProduct.OAUTH,
                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                StytchB2BProduct.SSO,
                StytchB2BProduct.PASSWORDS,
            ),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns listOf(mockk(relaxed = true))
            },
        expected =
            listOf(
                ProductComponent.OAuthButtons,
                ProductComponent.Divider,
                ProductComponent.PasswordEMLCombined,
                ProductComponent.Divider,
                ProductComponent.SSOButtons,
            ),
    )

internal val TEST_INPUTS_LAST =
    ProductComponentOrderingTestCase(
        name = "Inputs last",
        products = listOf(StytchB2BProduct.OAUTH, StytchB2BProduct.SSO, StytchB2BProduct.EMAIL_MAGIC_LINKS),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization =
            mockk {
                every { ssoActiveConnections } returns listOf(mockk(relaxed = true))
            },
        expected =
            listOf(
                ProductComponent.OAuthButtons,
                ProductComponent.SSOButtons,
                ProductComponent.Divider,
                ProductComponent.EmailForm,
            ),
    )

internal val TEST_MULTI_INPUTS_ONE_COMPONENT =
    ProductComponentOrderingTestCase(
        name = "Multiple inputs render one component",
        products = listOf(StytchB2BProduct.EMAIL_MAGIC_LINKS, StytchB2BProduct.EMAIL_OTP, StytchB2BProduct.PASSWORDS),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization = null,
        expected = listOf(ProductComponent.PasswordEMLCombined),
    )

internal val TEST_EML_DISCOVERY =
    ProductComponentOrderingTestCase(
        name = "EML Discovery",
        products = listOf(StytchB2BProduct.EMAIL_MAGIC_LINKS),
        authFlowType = AuthFlowType.DISCOVERY,
        organization = null,
        expected = listOf(ProductComponent.EmailDiscoveryForm),
    )

internal val TEST_EOTP_DISCOVERY =
    ProductComponentOrderingTestCase(
        name = "EOTP Discovery",
        products = listOf(StytchB2BProduct.EMAIL_OTP),
        authFlowType = AuthFlowType.DISCOVERY,
        organization = null,
        expected = listOf(ProductComponent.EmailDiscoveryForm),
    )

internal val TEST_EMAIL_PASSWORD_COMBINED =
    ProductComponentOrderingTestCase(
        name = "Email & Password Combined",
        products = listOf(StytchB2BProduct.EMAIL_MAGIC_LINKS, StytchB2BProduct.PASSWORDS),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization = null,
        expected = listOf(ProductComponent.PasswordEMLCombined),
    )

internal val TEST_PASSWORD_ONLY =
    ProductComponentOrderingTestCase(
        name = "Password Only",
        products = listOf(StytchB2BProduct.PASSWORDS),
        authFlowType = AuthFlowType.ORGANIZATION,
        organization = null,
        expected = listOf(ProductComponent.PasswordsEmailForm),
    )
