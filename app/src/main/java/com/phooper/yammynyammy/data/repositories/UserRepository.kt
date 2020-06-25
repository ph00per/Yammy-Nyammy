package com.phooper.yammynyammy.data.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.phooper.yammynyammy.data.db.dao.CartProductsDao
import com.phooper.yammynyammy.data.models.ProductInCart
import com.phooper.yammynyammy.data.models.User
import com.phooper.yammynyammy.utils.Constants.Companion.USERS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestone: FirebaseFirestore,
    private val cartProductsDao: CartProductsDao
) {

    suspend fun getCurrentUser(): FirebaseUser? = withContext(IO) { firebaseAuth.currentUser }

    suspend fun signInViaEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInViaGoogle(signInAccount: GoogleSignInAccount?): AuthResult? {
        val credential =
            GoogleAuthProvider.getCredential(signInAccount?.idToken, null)
        return try {
            firebaseAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signOut() = withContext(IO) { firebaseAuth.signOut() }

    suspend fun signUpViaEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun addUserData(uid: String, data: User): Boolean? {
        return try {
            firebaseFirestone.collection(USERS).document(uid).set(data).await()
            true
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserData(uid: String): DocumentSnapshot? {
        return try {
            firebaseFirestone.collection(USERS).document(uid).get().await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addProductToCart(productId: Int, count: Int) {
        if (cartProductsDao.getProductById(productId) == null) {
            cartProductsDao.addToCart(ProductInCart(productId, count))
        } else {
            cartProductsDao.increaseProductCount(productId, count)
        }
    }

    suspend fun removeProductFromCart(productInTheCart: ProductInCart) {
        if (cartProductsDao.getProductById(productInTheCart.productId)?.count == 1) {
            cartProductsDao.deleteProductById(productInTheCart.productId)
        } else {
            cartProductsDao.decreaseProductCount(productInTheCart.productId, productInTheCart.count)
        }
    }

    fun getAllCartProducts() = cartProductsDao.getAll()

}

